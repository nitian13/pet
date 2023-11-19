package org.nb.pethome.controller;

import org.nb.pethome.entity.Employee;
import org.nb.pethome.entity.Shop;
import org.nb.pethome.net.NetCode;
import org.nb.pethome.net.NetResult;
import org.nb.pethome.service.IShopService;
import org.nb.pethome.utils.ResultGenerator;
import org.nb.pethome.utils.StringUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shop")
public class ShopController {

    private IShopService iShopService;

    public ShopController(IShopService iShopService) {
        this.iShopService = iShopService;
    }

    //商铺注册
    @PostMapping("/register")
    public NetResult shopRegister(@RequestBody Shop shop) {
        if (StringUtil.isEmpty(shop.getName())) {
            return ResultGenerator.genErrorResult(NetCode.SHOP_NAME_INVALID, "店铺名不能为空");
        }
        if (StringUtil.isEmpty(shop.getTel())) {
            return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, "手机号不能为空");
        }
        if (StringUtil.isEmpty(shop.getAddress())) {
            return ResultGenerator.genErrorResult(NetCode.SHOP_ADDRESS_INVALID, "地址名不能为空");
        }
        if (StringUtil.isEmpty(shop.getLogo())) {
            return ResultGenerator.genErrorResult(NetCode.SHOP_LOGO_INVALID, "logo不能为空");
        }
        if (shop.getAdmin() == null) {
            Employee employee = new Employee();
            employee.setId(0l);
            shop.setAdmin(employee);
        }
        shop.setRegisterTime(System.currentTimeMillis());
        int count = iShopService.add(shop);
        if (count != 1) {
            return ResultGenerator.genFailResult("添加shop失败");
        }
        return ResultGenerator.genSuccessResult();
    }

    @RequestMapping("/list")
    public NetResult list() {
        return ResultGenerator.genSuccessResult(iShopService.list());
    }

    @PostMapping("/delete")
    public NetResult delete(Long id) {
        try {
            iShopService.remove(id);
            return ResultGenerator.genSuccessResult("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genErrorResult(NetCode.REMOVE_DEPARTMENT_ERROR, "删除失败！" + e.getMessage());
        }
    }
    //审核失败
    @PostMapping("/auditFalse")
    public NetResult auditFalse(Long id) {
        try {
            iShopService.auditFalse(id);
            return ResultGenerator.genSuccessResult("审核失败");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genErrorResult(NetCode.REMOVE_DEPARTMENT_ERROR, "error" + e.getMessage());
        }
    }


    //通过审核
    @PostMapping("/auditTrue")
    public NetResult auditTrue(Long id) {
        try {
            iShopService.auditTrue(id);
            return ResultGenerator.genSuccessResult("审核成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genErrorResult(NetCode.REMOVE_DEPARTMENT_ERROR, "error！" + e.getMessage());
        }
    }


    @PostMapping("/update")
    public NetResult shopUpdate(@RequestBody Shop shop) {
        try {
            if (StringUtil.isEmpty(shop.getName())) {
                return ResultGenerator.genErrorResult(NetCode.SHOP_NAME_INVALID, "店铺名不能为空");
            }
            if (StringUtil.isEmpty(shop.getTel())) {
                return ResultGenerator.genErrorResult(NetCode.PHONE_INVALID, "手机号不能为空");
            }
            if (StringUtil.isEmpty(shop.getAddress())) {
                return ResultGenerator.genErrorResult(NetCode.SHOP_ADDRESS_INVALID, "地址名不能为空");
            }
            iShopService.update(shop);
            return ResultGenerator.genSuccessResult(shop);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultGenerator.genErrorResult(NetCode.UPDATE_DEPARTMENT_ERROR, "修改失败！" + e.getMessage());
        }
    }

}
