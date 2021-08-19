package com.affirm.loans.model.internal;

import com.affirm.loans.model.response.Assignment;
import com.affirm.loans.model.response.Yield;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AssignmentYield {
    List<Assignment> assignments;
    List<Yield> yields;
}
