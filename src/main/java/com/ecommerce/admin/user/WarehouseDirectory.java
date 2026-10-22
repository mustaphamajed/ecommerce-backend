package com.ecommerce.admin.user;

import java.util.Collection;
import java.util.List;

public interface WarehouseDirectory {

    List<WarehouseSummary> findAll();

    List<WarehouseSummary> findByIds(Collection<Long> ids);
}
