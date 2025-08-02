package com.afa.devicer.back.dto.orders;

import com.afa.devicer.back.dto.BasePagedFilter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings({"PMD.CommentRequired", "PMD.TooManyFields"})
public class OrderPagedFilter extends BasePagedFilter {

    @Schema(description = "order id")
    private UUID orderId;

}
