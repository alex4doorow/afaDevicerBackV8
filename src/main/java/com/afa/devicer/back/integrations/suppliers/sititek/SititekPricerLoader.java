package com.afa.devicer.back.integrations.suppliers.sititek;

import com.afa.core.dto.integrations.suppliers.sititek.SititekMetadataDto;
import com.afa.core.dto.integrations.suppliers.sititek.SititekProductDto;
import com.afa.core.dto.products.ProductDto;
import com.afa.core.enums.DevicerErrors;
import com.afa.core.exceptions.DevicerException;
import com.afa.core.utils.NumericHelper;
import com.afa.devicer.back.config.CacheConfig;
import com.afa.devicer.back.entities.products.IProduct;
import com.afa.devicer.back.entities.products.Product;
import com.afa.devicer.back.integrations.suppliers.BasePricerLoader;
import com.afa.devicer.back.integrations.suppliers.PricerLoaderIF;
import com.afa.devicer.back.mappers.dictionaries.ProductMapper;
import com.afa.devicer.back.services.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Slf4j
@Service
public class SititekPricerLoader extends BasePricerLoader<SititekPricerConnector> implements PricerLoaderIF {

    private final String fileName;
    private final String directory;
    private final Boolean enabled;

    private final IProduct iProduct;
    private final ProductService productService;
    private final ProductMapper productMapper;

    /**
     * price product sku = key
     */
    private final Map<String, SititekMetadataDto> translatorData = new HashMap<>();

    public SititekPricerLoader(
            final SititekPricerConnector connector,
            @Value("${integrations.suppliers.sititek.feed.file}") final String fileName,
            @Value("${integrations.suppliers.sititek.feed.dir.in}") final String directory,
            @Value("${integrations.suppliers.sititek.enabled:false}") final boolean enabled,
            final IProduct iProduct,
            final ProductService productService,
            final ProductMapper productMapper) {

        super(connector);
        this.fileName = fileName;
        this.directory = directory;
        this.enabled = enabled;
        this.iProduct = iProduct;
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @CacheEvict(cacheNames = CacheConfig.CACHE_PRODUCT, allEntries = true)
    @Transactional
    @Override
    public void update() {
        if (!enabled) {
            return;
        }
        log.info("SititekPricerLoader start");
        connector.download();
        createTranslator();
        loadFeedFromFile();
        log.info("SititekPricerLoader finish");
    }

    private void loadFeedFromFile() {

        final String path = directory + "/" + fileName;
        final int startIndex = 17;

        final List<SititekProductDto> priceProducts = new ArrayList<>();
        try (InputStream inputStream = Files.newInputStream(Path.of(path));
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            final Sheet sheet = workbook.getSheetAt(0);
            for (int i = startIndex; i <= sheet.getLastRowNum(); i++) {

                final Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                if (row.getCell(0) != null && row.getCell(0).getCellType() == CellType.STRING) {

                    final String quantity = row.getCell(0).getStringCellValue();
                    final String sku = row.getCell(1).getStringCellValue();
                    final String productName = row.getCell(2).getStringCellValue();
                    final String supplierPrice = row.getCell(9).getStringCellValue();

                    if (StringUtils.isBlank(productName) || StringUtils.isBlank(sku)) {
                        continue;
                    }

                    final Cell cell = row.getCell(10);
                    final String customerPrice = switch (cell.getCellType()) {
                        case STRING -> cell.getStringCellValue();
                        case NUMERIC -> String.valueOf(cell.getNumericCellValue());
                        default -> "";
                    };

                    final SititekProductDto priceProduct = SititekProductDto.builder()
                            .sku(sku)
                            .name(productName)
                            .quantity(getFeedQuantity(quantity))
                            .supplierPrice3(getFeedPrice(supplierPrice))
                            .customerPrice(getFeedPrice(customerPrice))
                            .build();
                    priceProducts.add(priceProduct);
                }
            }
        } catch (IOException e) {
            log.error("sititek download error", e);
            throw new DevicerException(DevicerErrors.INTEGRATION_SUPPLIER_SITITEK_FEED_DOWNLOAD_ERRORS, e);
        }
        updateProducts(priceProducts);
    }

    private void updateProducts(final List<SititekProductDto> data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        data.forEach(this::updateProductByPrice);
    }

    private void updateProductByPrice(final SititekProductDto priceProduct) {
        final SititekMetadataDto metadata = translatorData.get(priceProduct.getSku());
        if (metadata == null) {
            return;
        }
        final ProductDto productDto = metadata.getProduct();

        final int supplierQuantity = priceProduct.getQuantity();
        final BigDecimal price = priceProduct.getCustomerPrice();
        final BigDecimal supplierPrice = priceProduct.getSupplierPrice3();

        final Product product = productService.findByIdOrThrow(productDto.getId());
        product.setLongName(priceProduct.getName());
        product.getStock().setSupplierPrice(supplierPrice);
        product.getStock().setSupplierQuantity(supplierQuantity);
        product.setPrice(price);

        final int quantity;
        if (iProduct.existsMarketSellerByProductId(product.getId())) {
            quantity = product.getStock().getQuantity();
        } else {
            quantity = product.getStock().getQuantity() + product.getStock().getSupplierQuantity();
        }
        product.setQuantity(quantity);
        iProduct.saveAndFlush(product);
        log.info("loaded: {} [{}] - {}", product.getId(), productDto.getSku(), product.getShortName());
    }

    @SuppressWarnings({"PMD.LiteralsFirstInComparisons"})
    private int getFeedQuantity(final String quantity) {
        if (StringUtils.isEmpty(quantity) || quantity.trim().equals("-")) {
            return 0;
        }
        String localQuantity = quantity.trim();
        if (localQuantity.indexOf("+") > 0) {
            localQuantity = localQuantity.substring(0, localQuantity.length() - 1);
            return Integer.parseInt(localQuantity);
        }
        final String[] quantityParts = localQuantity.split("-");
        if (quantityParts.length == 0) {

            final String localQuantityClear = NumericHelper.numberDigit(localQuantity);
            return Integer.parseInt(localQuantityClear);
        } else {

            final String localQuantityClear = NumericHelper.numberDigit(quantityParts[0]);
            try {
                return Integer.parseInt(localQuantityClear);
            } catch (ArrayIndexOutOfBoundsException e) {
                log.error("localQuantityClear: {}", localQuantityClear, e);
            }
            return 0;
        }
    }

    private BigDecimal getFeedPrice(final String price) {

        try {
            if (StringUtils.isBlank(price) || "-".equalsIgnoreCase(price.trim())) {
                return BigDecimal.ZERO;
            }

            final String localPrice = price.trim()
                    .replace(" ", "")
                    .replace(",", ".");

            return new BigDecimal(localPrice);

        } catch (NumberFormatException e) {
            log.error("convert price error {}", price, e);
            throw new DevicerException(DevicerErrors.INTEGRATION_SUPPLIER_SITITEK_FEED_DOWNLOAD_ERRORS, e);
        }
    }

    private void translatorPut(final Integer productId,
                               final String priceSku,
                               final String priceName) {
        if (Objects.equals(productId, -1) || StringUtils.isBlank(priceSku)) {
            return;
        }
        final ProductDto productDto = productMapper.fromProduct(productService.findByIdOrThrow(productId.longValue()));
        translatorData.put(priceSku, new SititekMetadataDto(new SititekProductDto(priceSku, priceName), productDto));
    }

    @SuppressWarnings({"PMD.UnusedFormalParameter"})
    private void translatorPut(final Integer productId,
                               final String priceSku,
                               final String priceName,
                               final Boolean optionalExist,
                               final Integer linkId) {
        translatorPut(productId, priceSku, priceName);
    }

    private void createTranslator() {
        translatorPut(577, "", "Автокормушка SITITEK Pets Fish для аквариумных рыб");
        translatorPut(576, "", "Автокормушка SITITEK Pets Ice Mini (Light Blue) для животных");
        translatorPut(-1, "", "Автокормушка SITITEK Pets Ice Mini (Yellow) для животных", true, 576);
        translatorPut(128, "", "Автокормушка SITITEK Pets Maxi (Dark Blue) для животных");
        translatorPut(-1, "", "Автокормушка SITITEK Pets Maxi (Green) для животных", true, 128);
        translatorPut(-1, "", "Автокормушка SITITEK Pets Maxi (Yellow) для животных", true, 128);
        //
        translatorPut(578, "", "Автокормушка SITITEK Pets Pond для прудовых рыб");
        translatorPut(766, "", "Автокормушка SITITEK Pets Prо");
        translatorPut(765, "", "Автокормушка SITITEK Pets Prо Plus");
        translatorPut(130, "", "Автокормушка для кошек и собак SITITEK Pets Tower-10");
        translatorPut(129, "", "Автокормушка для кошек и собак SITITEK Pets Tower-5");
        translatorPut(346, "", "Автопоилка SITITEK Pets Uni для животных");
        translatorPut(131, "", "Автопоилка для кошек и собак SITITEK Pets Aqua 2");
        //
        translatorPut(550, "62250", "Алкотестер \"AlcoHunter Professional X\"");
        translatorPut(997, "", "Алкотестер \"AlcoHunter Professional X2\"");
        translatorPut(96, "", "Алкотестер \"AlcoHunter Professional+\"");
        translatorPut(148, "", "Алкотестер \"AlcoHunter Эконом\"");
        translatorPut(344, "", "Алкотестер SITITEK CA20F");
        translatorPut(551, "", "Алкотестер SITITEK PRO2");
        translatorPut(138, "", "Мундштук для алкотестера \"Универсальный\" круглый");
        //
        translatorPut(579, "", "Видеоняня SITITEK BM01");
        translatorPut(580, "", "Видеоняня SITITEK BM02");
        //
        translatorPut(231, "", "Видеоглазок SITITEK Eye");
        translatorPut(364, "", "Видеоглазок SITITEK i3");
        translatorPut(361, "", "Видеоглазок SITITEK i8");
        translatorPut(366, "", "Видеоглазок SITITEK Simple II");
        translatorPut(81, "", "Видеоглазок беспроводной \"Black Fortress\"");
        translatorPut(754, "", "Видеоглазок беспроводной SITITEK TE850H");
        translatorPut(824, "", "Видеоглазок SITITEK R26S");
        translatorPut(826, "", "Видеоглазок SITITEK R22A");
        translatorPut(825, "", "Видеоглазок SITITEK A21");
        //
        translatorPut(84, "", "Видеодомофон беспроводной KIVOS KDB300");
        translatorPut(753, "", "Монитор для видеодомофона \"KIVOS KDB300\"");
        //
        translatorPut(651, "", "Видеодомофон беспроводной KIVOS 303");
        translatorPut(654, "", "Видеодомофон беспроводной KIVOS 400");
        translatorPut(655, "", "Видеодомофон беспроводной KIVOS 700");
        translatorPut(656, "", "Видеодомофон беспроводной SITITEK Puck");
        //
        translatorPut(657, "", "Монитор для видеодомофона KIVOS KDB303");
        translatorPut(658, "", "Монитор для видеодомофона KIVOS KDB700");
        //
        translatorPut(438, "", "Зарядное уст-во на солнечных батареях \"SITITEK Sun-Battery Duos\"");
        translatorPut(105, "", "Зарядное уст-во на солнечных батареях \"SITITEK Sun-Battery SC-09\"");
        translatorPut(544, "", "Зарядное уст-во на солнечных батареях \"SITITEK Sun-Battery SC-10\" голубая");
        translatorPut(615, "", "Зарядное уст-во на солнечных батареях (рюкзак) \"SolarBag SB-267\"");
        translatorPut(616, "", "Зарядное уст-во на солнечных батареях (рюкзак) \"SolarBag SB-285\"");
        //
        translatorPut(281, "", "Индикатор поля \"BugHunter CR-1\" Карточка");
        translatorPut(624, "", "Индикатор поля \"BugHunter МИКРО\"");
        translatorPut(625, "", "Индикатор поля \"BugHunter Профессионал\" BH-02 RAPID");
        translatorPut(133, "", "Индикатор поля \"BugHunter Профессионал\" BH-03");
        translatorPut(594, "", "Индикатор поля \"BugHunter Профессионал\" BH-03 Expert");
        translatorPut(1081, "", "Индикатор поля \"BugHunter Профессионал\" BH-04");
        //
        translatorPut(445, "", "Нитратомер + солемер \"EcoLifePro2\"");
        //
        translatorPut(278, "51676", "Ножеточка электрическая SITITEK \"Хозяйка 31М\"");
        translatorPut(829, "", "Ножеточка электрическая SITITEK \"Хозяйка 31М\" PRO");
        translatorPut(294, "57150", "Ножеточка электрическая SITITEK \"Хозяйка 40М\"");
        translatorPut(547, "56053", "Ножеточка электрическая SITITEK \"Хозяйка 50М\"");
        //
        translatorPut(296, "", "Обнаружитель скрытых видеокамер \"BugHunter Dvideo Nano\"");
        translatorPut(154, "", "Обнаружитель скрытых видеокамер \"BugHunter Dvideo Эконом\"");
        translatorPut(135, "", "Обнаружитель скрытых видеокамер профессиональный \"BugHunter Dvideo\"");
        //
        translatorPut(54, "", "Аксессуар для отпугивателей грызунов и птиц \"ГРАД\" - адаптер питания от аккумулятора (12 В)");
        translatorPut(55, "", "Аксессуар для отпугивателей грызунов и птиц \"ГРАД\" - адаптер питания от прикуривателя");
        translatorPut(50, "55760", "Отпугиватель грызунов \"Град А-550УЗ\"");
        translatorPut(365, "", "Отпугиватель грызунов \"ГРАД УЛЬТРА 3D\"");
        translatorPut(47, "", "Отпугиватель грызунов и кротов \"Град А-500\" с адаптером 220В");
        translatorPut(51, "", "Отпугиватель грызунов и насекомых \"ГРАД А-1000 ПРО\"");
        translatorPut(52, "", "Отпугиватель грызунов и насекомых \"ГРАД А-1000 ПРО+\"");
        translatorPut(638, "", "Отпугиватель грызунов \"Град А-506\"");
        translatorPut(776, "", "Отпугиватель грызунов биологический \"SITITEK БИО-1\"");
        //
        translatorPut(552, "", "Отпугиватель грызунов универсальный \"SITITEK 360\"");
        translatorPut(540, "", "Отпугиватель грызунов и насекомых (ультразвуковой) \"Weitech WK-0600 CIX\"");
        //
        translatorPut(79, "", "Отпугиватель пылевого клеща ультразвуковой портативный \"MiteLess\"");
        translatorPut(60, "", "Отпугиватель клещей ультразвуковой \"Антиклещ М\"");
        //
        translatorPut(62, "", "Отпугиватель комаров \"Комарин – брелок Лайт\"");
        translatorPut(237, "", "Отпугиватель комаров \"Weitech WK-0029\"");
        translatorPut(341, "", "Отпугиватель ос \"Weitech WK-0432\"");
        //
        translatorPut(304, "", "Отпугиватель кротов \"SITITEK Гром-Профи LED+\"");
        translatorPut(33, "", "Отпугиватель кротов \"SITITEK Гром-Профи М\"");
        translatorPut(844, "", "Отпугиватель змей \"Weitech WK2030\"");
        translatorPut(558, "", "Отпугиватель кротов \"Weitech WK-0677\"");
        //
        translatorPut(471, "", "Аксессуар для отпугивателей птиц \"Zon EL08\" стойка");
        translatorPut(475, "", "Аксессуар для отпугивателей птиц \"Zon Mark 4\" стойка");
        translatorPut(572, "", "Отпугиватель птиц \"Balcony Gard\"");
        translatorPut(454, "", "Отпугиватель птиц \"ГРАД А-16\"");
        translatorPut(1074, "", "Отпугиватель птиц \"ГРАД А-16 ПРО\"");
        translatorPut(463, "", "Аксессуар для отпугивателя птиц \"ГРАД А-16 / А-16 PRO\" внешний динамик 100Вт (30м)");
        translatorPut(1295, "", "Аксессуар для отпугивателя птиц \"ГРАД А-16 / А-16 PRO\" внешний динамик 100Вт (5м)");
        translatorPut(1296, "", "Аксессуар для отпугивателя птиц \"ГРАД А-16 / А-16 PRO\" внешний динамик 100Вт (60м)");
        translatorPut(1297, "", "Аксессуар для отпугивателя птиц \"ГРАД А-16\" Разветвитель на 2 гнезда jack 3,5 мм");
        //
        translatorPut(1294, "", "Отпугиватель птиц \"SITITEK ХИЩНИК\" динамический");
        translatorPut(1293, "", "Отпугиватель птиц \"SITITEK ХИЩНИК\" с флагштоком 4,5 м (комплект)");
        translatorPut(1282, "", "Отпугиватель птиц \"SITITEK ХИЩНИК\" с флагштоком 6,3 м (комплект)");
        //
        //translatorPut(287, "", "Отпугиватель птиц \"Шипы противоприсадные \"SITITEK Барьер-Премиум\"");
        //translatorPut(181, "", "Отпугиватель птиц \"Шипы противоприсадные \"SITITEK Барьер\" Пластик");
        //translatorPut(877, "", "Отпугиватель птиц \"Шипы противоприсадные \"SITITEK Барьер-Премиум 2\"");
        translatorPut(1298, "", "Отпугиватель птиц \"Шипы противоприсадные \"SITITEK Барьер 2П\"");
        //translatorPut(882, "", "Отпугиватель птиц \"Шипы противоприсадные \"SITITEK Барьер-Премиум 2М\" Металл");
        //translatorPut(816, "", "Отпугиватель птиц \"Шипы противоприсадные \"SITITEK Барьер-Премиум 3\"");
        translatorPut(1073, "", "Отпугиватель птиц \"Шипы противоприсадные \"SITITEK Барьер-Премиум 3R\"");
        //translatorPut(817, "", "Отпугиватель птиц \"Шипы противоприсадные \"SITITEK Барьер-Премиум 5\"");
        translatorPut(957, "", "Отпугиватель птиц \"ПЗУ Барьер-Премиум ЛЭП\"");
        //
        translatorPut(1072, "", "Отпугиватель птиц визуальный \"Орел\"");
        translatorPut(1299, "", "Отпугиватель птиц визуальный \"Сова\"");
        translatorPut(1300, "", "Отпугиватель птиц визуальный \"Сова-М\" звуковой");
        //
        translatorPut(474, "", "Отпугиватель птиц гром-пушка механическая \"Zon Mark 4 Telescope\"");
        translatorPut(473, "", "Отпугиватель птиц гром-пушка механическая \"Zon Mark 4\" Single Megaphone");
        translatorPut(470, "", "Отпугиватель птиц гром-пушка электронная \"Zon EL08 Telescope\"");
        translatorPut(472, "", "Отпугиватель птиц гром-пушка электронная \"Zon EL08\" Single Megaphone");
        translatorPut(1064, "", "Комплект отпугивателя птиц \"Zon EL08 Single Megaphone\" с пультом ДУ");
        translatorPut(35, "", "Отпугиватель птиц \"Weitech WK-0020\"");
        translatorPut(1080, "", "Отпугиватель птиц \"Weitech WK-0025\"");
        translatorPut(897, "", "Отпугиватель птиц \"Weitech WK-0108\"");
        //
        translatorPut(58, "", "Отпугиватель собак \"SITITEK ГРОМ-125\"");
        translatorPut(57, "", "Отпугиватель собак \"SITITEK ГРОМ-250M\"");
        translatorPut(56, "", "Отпугиватель собак \"Собакам.Нет Вспышка+\"");
        translatorPut(59, "", "Отпугиватель собак \"Собакам.Нет\"");
        translatorPut(643, "", "Отпугиватель собак стационарный \"Grad Duos S\"");
        translatorPut(726, "", "Отпугиватель собак стационарный \"Weitech WK0052\"");
        translatorPut(146, "", "Отпугиватель собак стационарный \"Weitech WK0053\"");
        translatorPut(704, "", "Отпугиватель собак стационарный \"Weitech WK0055\"");
        translatorPut(1079, "", "Отпугиватель собак стационарный \"Weitech WK0054\"");
        translatorPut(725, "", "Аксессуар для отпугивателей собак Weitech-0052/0054/0055 - адаптер питания WKT052-OUT");
        //
        //translatorPut(640, "", "Диск проекционный Homestar \"Галактика\"");
        //translatorPut(194, "", "Диск проекционный Homestar \"Земля в космосе\"");
        //translatorPut(195, "", "Диск проекционный Homestar \"Созвездия\"");
        //translatorPut(641, "", "Диск проекционный SITITEK для планетариев Homestar \"Земля и Луна\"");
        //translatorPut(573, "", "Диск проекционный SITITEK для планетариев Homestar \"Романтический\"");
        //translatorPut(193, "", "Диск проекционный для Homestar Lite \"Под водой\"");
        //
        //translatorPut(214, "", "Планетарий HomeStar Aurora Alaska (черный)");
        translatorPut(-1, "", "Планетарий HomeStar Aurora Alaska (белый)", true, 214);
        //
        //translatorPut(117, "", "Планетарий HomeStar Classic");
        //translatorPut(257, "", "Планетарий HomeStar R2-D2");
        //translatorPut(644, "", "Планетарий HomeStar Resort (голубой)");
        //translatorPut(139, "", "Планетарий SITITEK AstroEye");
        //translatorPut(639, "", "Планетарий SITITEK Media");
        //
        translatorPut(918, "", "Подавитель диктофонов, микрофонов \"BugHunter DAudio bda-2 Ultrasonic\"");
        translatorPut(727, "", "Подавитель диктофонов, микрофонов \"BugHunter DAudio bda-3 Voices\"");
        translatorPut(996, "", "Подавитель диктофонов, микрофонов \"BugHunter DAudio bda-3 Voices\" с ПДУ");
        translatorPut(1032, "", "Подавитель диктофонов, микрофонов \"BugHunter DAudio bda-4\"");
        translatorPut(1301, "", "Подавитель диктофонов, микрофонов \"BugHunter DAudio bda-5\"");
        translatorPut(728, "", "Ультразвуковая колонка \"BugHunter DAudio bda-3 Voices\"");
        translatorPut(894, "", "Аксессуар для подавителя \"BugHunter DAudio bda\" Клатч");
        //
        translatorPut(541, "", "Пуско-зарядное устройство \"SITITEK SolarStarter 18 000\"");
        translatorPut(-1, "", "Пусковой инвертор \"JumpStarter Q3\"");
        //
        translatorPut(80, "", "Робот-пылесос Robo-sos X500");
        //
        translatorPut(85, "", "Столик для ноутбука SITITEK Bamboo 1");
        translatorPut(86, "", "Столик для ноутбука SITITEK Bamboo 2");
        //
        translatorPut(955, "", "Аксессуар для уничтожителей комаров Grad Black брикет приманка-аттрактант \"G-mosquito\"");
        translatorPut(234, "", "Аксессуар для уничтожителей комаров \"Octenol\" в бутылке 10 мл");
        translatorPut(234, "", "Аттрактант-приманка \"SITITEK\" для уничтожителей комаров");
        translatorPut(235, "", "Аксессуар для уничтожителя комаров \"Нонаналь\"");
        //
        translatorPut(709, "", "Уничтожитель комаров \"Weitech WK 8202\"");
        translatorPut(61, "", "Уничтожитель комаров и других насекомых \"SITITEK Москито-MV-01\"");
        translatorPut(680, "", "Уничтожитель комаров и других насекомых \"SITITEK Москито-MV-11\"");
        translatorPut(679, "", "Уничтожитель комаров Установка \"GRAD BLACK G1\"");
        translatorPut(864, "", "Уничтожитель комаров Установка \"GRAD BLACK G2\" с доп. аттрактантом \"G-Mosquito\"");
        translatorPut(437, "", "Уничтожитель личинок комаров биологический \"Биоларвицид-100\"");
        //
        translatorPut(64, "", "Уничтожитель комаров Установка \"Комарам.Нет KRN-5000 PRO\"");
        translatorPut(348, "", "Уничтожитель комаров Установка \"Комарам.Нет KRN-5000 Турбо PRO\"");
        //
        translatorPut(999, "", "Видеокамера для рыбалки SITITEK FishCam-700 DVR 15м с функцией записи");
        translatorPut(697, "", "Видеокамера для рыбалки SITITEK FishCam-400 DVR 15м с функцией записи");
        //
        translatorPut(941, "", "Стерилизатор портативный SITITEK БИО-2");
        translatorPut(942, "", "Аксессуар для производства стерилизатора SITITEK БИО-2 диз.средство");
        translatorPut(943, "", "Лампа УФ мягкого действия SITITEK UV-1");
        translatorPut(944, "", "Рециркулятор воздуха бактерицидный UVC-1");
        translatorPut(1024, "", "Рециркулятор воздуха бактерицидный UVC-2 с озоном");
        //
        translatorPut(928, "", "Маска многоразовая HEPA Черная");
        translatorPut(930, "", "Маска многоразовая марлевая Белая");
        translatorPut(929, "", "Маска многоразовая х/б разноцветная \"Маски-Микс\"");
        translatorPut(927, "", "Маска многоразовая Черная (Bona Fide: Mask BF \"Black\")");
        //
        translatorPut(1041, "", "Аксессуар для инкубатора SITITEK 48,96,112: горизонтальные лотки в сборе на 48 яиц");
        translatorPut(1042, "", "Аксессуар для инкубатора SITITEK 48,96,112: универсальные лотки в сборе на 36/144 яиц");
        translatorPut(1050, "", "Аксессуар для инкубатора/брудера Автокормушка");
        translatorPut(1049, "", "Аксессуар для инкубатора/брудера Автопоилка");
        translatorPut(1043, "", "Брудер для цыплят \"SITITEK HD 35W\"");
        translatorPut(1040, "", "Инкубатор автоматический \"SITITEK 112\"");
        translatorPut(1045, "", "Инкубатор автоматический \"SITITEK 12\"");
        translatorPut(1048, "", "Инкубатор автоматический \"SITITEK 32\"");
        translatorPut(1047, "", "Инкубатор автоматический \"SITITEK 48\"");
        translatorPut(1046, "", "Инкубатор автоматический \"SITITEK 56\"");
        translatorPut(1039, "", "Инкубатор автоматический \"SITITEK 96\"");
        translatorPut(1056, "", "Инкубатор автоматический \"SITITEK 196\"");
        translatorPut(1062, "", "Инкубатор автоматический \"SITITEK 128\"");
        translatorPut(1061, "", "Инкубатор автоматический \"SITITEK 64\"");
        //
        translatorPut(462, "", "Сигнализация \"Лающая собака\" (PA-02)");
        //
        translatorPut(1027, "", "Сейф автомобильный биометрический \"Ospon 100A\"");
        translatorPut(1025, "", "Сейф автомобильный \"Ospon 300C\" с кодовым замком");
    }
}