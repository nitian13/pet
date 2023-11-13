package org.nb.pethome.service;

import org.apache.ibatis.annotations.Param;
import org.nb.pethome.entity.Shop;

import java.util.List;

public interface IShopService {

    int add(Shop shop);
    List<Shop> list();
    void remove(Long id);
    void successfulAudit(Long id);
    void auditFailure(Long id);
    void update(Shop shop);

    List<Shop> paginationList(@Param("offset") int offset, @Param("pageSize") int pageSize);

    int count();
    Shop findById(long id);
    Shop findByAddress(String address);
}
