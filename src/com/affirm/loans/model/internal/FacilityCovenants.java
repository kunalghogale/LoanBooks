package com.affirm.loans.model.internal;

import com.affirm.loans.model.request.Covenant;
import com.affirm.loans.model.request.Facility;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class FacilityCovenants {
    Facility facility;
    List<Covenant> covenants;
}
