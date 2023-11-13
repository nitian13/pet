package org.nb.pethome.controller;

import org.nb.pethome.entity.*;
import org.nb.pethome.net.NetCode;
import org.nb.pethome.net.NetResult;
import org.nb.pethome.net.param.AddPetParam;
import org.nb.pethome.service.*;
import org.nb.pethome.utils.AddressDistanceComparator;
import org.nb.pethome.utils.GaoDeMapUtil;
import org.nb.pethome.utils.ResultGenerator;
import org.nb.pethome.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private ISeekingTheLordService iSeekingTheLordService;

    private IEmployeeService iEmployeeService;

    private IShopService iShopService;

    private IUsersService iUsersService;

    private IPetService iPetService;

    @Autowired
    public UserController (ISeekingTheLordService iSeekingTheLordService,IEmployeeService iEmployeeService,IShopService iShopService,IUsersService iUsersService,IPetService iPetService){
        this.iSeekingTheLordService=iSeekingTheLordService;
        this.iEmployeeService = iEmployeeService;
        this.iShopService = iShopService;
        this.iUsersService = iUsersService;
        this.iPetService=iPetService;
    }

    @PostMapping("/publish")
    public NetResult Publish(@RequestBody AddPetParam addPetParam){
        SeekingTheLord seekingTheLord = addPetParam.seekingTheLord;
        int user_id = addPetParam.user_id;
        if (StringUtil.isEmpty(seekingTheLord.getName())){
            return ResultGenerator.genErrorResult(NetCode.PET_NAME_INVALID, "宠物名不能空");
        }

        if (StringUtil.isEmpty(seekingTheLord.getAddress())){
            return ResultGenerator.genErrorResult(NetCode.ADDRESS_INVALID, "地址不能空");
        }

        if (StringUtil.isEmpty(seekingTheLord.getSex())){
            return ResultGenerator.genErrorResult(NetCode.SEX_INVALID, "性别不能空");
        }

        if (seekingTheLord.getIsInoculation()<0){
            return ResultGenerator.genErrorResult(NetCode.ISINOCULATION_INVALID, "接种信息错误");
        }

        if (seekingTheLord.getBirth()<0){
            return  ResultGenerator.genErrorResult(NetCode.BIRTH_INVALID, "生日错误");
        }

        if (iUsersService.findById(user_id)==null){
            return ResultGenerator.genErrorResult(NetCode.ID_INVALID, "没有这个用户id");
        }


        List<Shop> shopList = iShopService.list();
        List<Location> locations = new ArrayList<>();

        try {
            //把店铺地址加到list
            for (Shop value : shopList) {
                locations.add(GaoDeMapUtil.getLngAndLag(value.getAddress()));
            }
            //获取用户发信息的地址
            Location userLocation = GaoDeMapUtil.getLngAndLag(seekingTheLord.getAddress());
            //获取离用户最近的店铺的地址
            Location latest = AddressDistanceComparator.findNearestAddress(userLocation,locations);
            //找到这个店铺并绑定
            Shop shop = iShopService.findByAddress(latest.getFormattedAddress());
            seekingTheLord.setShop(shop);
            //添加时间
            seekingTheLord.setCreateTime(System.currentTimeMillis());
            //绑定店铺 admin 账号
            System.out.println(shop.getAdmin_id());
            Employee admin = iEmployeeService.findById(shop.getAdmin_id());
            seekingTheLord.setAdmin(admin);
            //绑定宠物类型
            Pet pet = iPetService.findById(seekingTheLord.getPet_id());
            seekingTheLord.setPet(pet);
            //绑定user
            Users users = iUsersService.findById(user_id);
            seekingTheLord.setUsers(users);

            int result =iSeekingTheLordService.add(seekingTheLord);
            if (result!=1){
                return ResultGenerator.genFailResult("添加失败");
            }
            System.out.println(shop.getId());
            System.out.println(admin.getId());
            System.out.println(pet.getId());
            System.out.println(users.getId());
            System.out.println(seekingTheLord.getId());
            int result1 = iSeekingTheLordService.addTask(shop.getId(),admin.getId(),pet.getId(),users.getId(),seekingTheLord.getId());
            if (result1!=1){
                return ResultGenerator.genFailResult("添加失败");
            }
            return ResultGenerator.genSuccessResult(pet);
        }catch (Exception e){
            e.printStackTrace();
        }

        return ResultGenerator.genFailResult("添加失败");
    }
}
