package com.epam.rd.advphone.repositories;

import com.epam.rd.advphone.models.Call;
import java.util.List;

public interface CallsProvider {
    List<Call> getCalls();
    void deleteCalls(int id);
}
