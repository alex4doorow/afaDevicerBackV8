package com.afa.devicer.back.dto.orders;

import com.afa.devicer.back.dto.persons.PersonShortDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order")
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("PMD.TooManyFields")
public class OrderDto {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "order num")
    private Long orderNum;

    @Schema(name = "order_date")
    private LocalDate orderDate;

    //...

    @Schema(description = "автор")
    private PersonShortDto userAdded;

    @Schema(description = "создано", example = "2024-06-16T07:42:45Z")
    private Instant dateAdded;


}
