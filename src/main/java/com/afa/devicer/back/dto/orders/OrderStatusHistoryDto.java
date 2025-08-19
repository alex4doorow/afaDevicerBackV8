package com.afa.devicer.back.dto.orders;

import com.afa.devicer.back.dto.persons.PersonShortDto;
import com.afa.devicer.back.enums.OrderStatusTypes;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order status history info")
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("PMD.TooManyFields")
public class OrderStatusHistoryDto {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "status")
    private OrderStatusTypes status;

    @Schema(description = "crm status")
    private String crmStatus;

    @Schema(description = "crm sub status")
    private String crmSubStatus;

    @Schema(description = "user added")
    private PersonShortDto userAdded;

    @Schema(description = "date added")
    private Instant dateAdded;
}
