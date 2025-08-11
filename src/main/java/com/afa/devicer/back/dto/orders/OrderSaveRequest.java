package com.afa.devicer.back.dto.orders;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order")
public class OrderSaveRequest {

    @NotNull
    @Size(min = 1)
    @Schema(description = "order num")
    private Long orderNum;

    @NotNull
    @Schema(description = "order_date")
    private LocalDate orderDate;

    @NotNull
    @Schema(description = "customer id")
    private Long customerId;


//
//    @NotNull
//    @Schema(description = "запрос")
//    private UUID projectId;
//
//    @NotNull
//    @Schema(description = "направление")
//    private UUID competenceCategoryId;
//
//    @NotNull
//    @Schema(description = "специализация")
//    private UUID competenceId;
//
//    @NotNull
//    @Min(value = 1, message = "Значение должно быть больше 0")
//    @Schema(description = "specialist quantity", example = "1")
//    private Integer specialistQnt;
//
//    @NotBlank
//    @Size(max = 1024)
//    @Schema(description = "задачи", example = "for save Earth")
//    private String objectives;
//
//    @Size(max = 1024)
//    @Schema(description = "будет плюсом")
//    private String niceToHave;
//
//    @NotNull
//    @Schema(description = "валюта покупки")
//    private UUID buyCurrencyId;
//
//    @Min(value = 1, message = "Ставка покупки должна быть положительной")
//    @Schema(description = "ставка покупки")
//    private Integer buyRateMax;
//
//    @NotNull
//    @Schema(description = "валюта продажи")
//    private UUID saleCurrencyId;
//
//    @Min(value = 1, message = "Ставка продажи должна быть положительной")
//    @Schema(description = "ставка продажи")
//    private Integer saleRateMax;
//
//    @Size(min = 1, max = 2)
//    @NotNull
//    @Schema(description = "грейды")
//    private Set<UUID> grades;
//
//    @Schema(description = "ключевые навыки")
//    private Set<UUID> skills;
//
//    @Size(min = 1)
//    @NotNull
//    @Schema(description = "опыт")
//    private List<@Size(min = 2, max = 1024, message = "Длина строки должна быть от {min} до {max} символов.") String> experiences;
}
