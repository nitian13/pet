package org.nb.pethome.controller;

import org.nb.pethome.common.Constants;
import org.nb.pethome.entity.*;
import org.nb.pethome.net.NetCode;
import org.nb.pethome.net.NetResult;
import org.nb.pethome.net.param.SeekingTheLordParam;
import org.nb.pethome.service.*;
import org.nb.pethome.utils.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
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
    private RedisTemplate redisTemplate;

    @Autowired
    public UserController (ISeekingTheLordService iSeekingTheLordService,IEmployeeService iEmployeeService,IShopService iShopService,IUsersService iUsersService,IPetService iPetService,RedisTemplate redisTemplate){
        this.iSeekingTheLordService=iSeekingTheLordService;
        this.iEmployeeService = iEmployeeService;
        this.iShopService = iShopService;
        this.iUsersService = iUsersService;
        this.iPetService=iPetService;
        this.redisTemplate=redisTemplate;

    }


    @PostMapping("/seektheload")
    public NetResult seekTheLoad(@RequestBody SeekingTheLordParam seekingTheLordParam,HttpServletRequest request){
        //从请求头那里获得用户的token
        String token = request.getHeader("token");
        //你通过token拿的数据可能为空
        Users user = null;
        //从redis获得用户数据
        Object userObject = redisTemplate.opsForValue().get(token);
        //数据为空
        if (userObject == null) {
            return ResultGenerator.genErrorResult(NetCode.TOKEN_NOT_EXIST, Constants.TOKEN_IS_NULL);
        }
        user = (Users) userObject;
        System.out.println(user);

        //添加正在登录用户的id
        seekingTheLordParam.setUser_id(user.getId());
        System.out.println(seekingTheLordParam.getUser_id());

        //名字不能空
        if (StringUtil.isEmpty(seekingTheLordParam.getName())){
            return ResultGenerator.genErrorResult(NetCode.PET_NAME_INVALID, Constants.PETNAME_IS_NULL);
        }

        //地址不能空
        if (StringUtil.isEmpty(seekingTheLordParam.getAddress())){
            return ResultGenerator.genErrorResult(NetCode.ADDRESS_INVALID, Constants.ADDRESS_IS_NULL);
        }

        //是否接种必须为0或1，否则接种数据错误
        if (seekingTheLordParam.getIsInoculation()!=0&&seekingTheLordParam.getIsInoculation()!=1){
            return ResultGenerator.genErrorResult(NetCode.ISINOCULATION_INVALID, Constants.INOCULATION_ERROR);
        }
        System.out.println(111);

        //宠物的性别必须为0或者1，否则就性别错误
        if (seekingTheLordParam.getSex()!=0&&seekingTheLordParam.getSex()!=1){
            return ResultGenerator.genErrorResult(NetCode.SEX_INVALID, Constants.SEX_ERROR);
        }
        System.out.println(2);

        //宠物的价格不能为负数，否则就价格错误
        if (seekingTheLordParam.getPrice()<0){
            return ResultGenerator.genErrorResult(NetCode.PRICE_INVALID, Constants.PRICE_ERROR);
        }

        //宠物的出生日期不能为负和0
        if (seekingTheLordParam.getBirth()<=0){
            return  ResultGenerator.genErrorResult(NetCode.BIRTH_INVALID, Constants.BIRTH_ERROR);
        }

        //获取所有商铺
        List<Shop> shopList = iShopService.list();
        //创建一个集存放地址的集合
        List<Location> locations = new ArrayList<>();


            //把所有店铺的地址加到集合里
            for (Shop s : shopList) {
                try {
                    locations.add(GaoDeMapUtil.getLngAndLag(s.getAddress()));
                } catch (UnsupportedEncodingException e) {
                    LoggerFactory.getLogger(UserController.class).error(e.getMessage());
                }
            }
        System.out.println(666);
        System.out.println(locations.toString());
            //获取用户发布寻主的地址
            Location userLocation = null;
            try {
                userLocation = GaoDeMapUtil.getLngAndLag(seekingTheLordParam.getAddress());
            } catch (UnsupportedEncodingException e) {
                return ResultGenerator.genErrorResult(NetCode.ADDRESS_INVALID, "地址错误");
            }
        System.out.println(userLocation);
            //获取离用户最近的店铺的地址
            Location nearLatest = AddressDistanceComparator.findNearestAddress(userLocation, locations);
        System.out.println(nearLatest);
            //通过最近的地址去找到商铺，并且绑定
            Shop shop = iShopService.findByAddress(nearLatest.getFormattedAddress());
            //不能100%找到商铺
            if(shop==null){
            return ResultGenerator.genErrorResult(NetCode.SHOP_INVALID, "无法找到商户");
        }
            //通过商铺的id绑定商铺
            seekingTheLordParam.setShop_id(shop.getId());
            //通过管理员id绑定管理员
            seekingTheLordParam.setAdmin_id(shop.getAdmin_id());
            //设置当前的时间
            seekingTheLordParam.setCreateTime(System.currentTimeMillis());

            //寻主
            int num =iSeekingTheLordService.add(seekingTheLordParam);
            //添加失败则寻主不成功
            if (num!=1){
                System.out.println(seekingTheLordParam);
                return ResultGenerator.genFailResult("添加失败");
            }
            //反之，则寻主成功
            return ResultGenerator.genSuccessResult(seekingTheLordParam);
    }



}
