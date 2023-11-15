package org.nb.pethome.controller;

import org.nb.pethome.common.Constants;
import org.nb.pethome.entity.*;
import org.nb.pethome.interceptor.TokenInterceptor;
import org.nb.pethome.net.NetCode;
import org.nb.pethome.net.NetResult;
import org.nb.pethome.net.param.AddPetParam;
import org.nb.pethome.service.*;
import org.nb.pethome.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    private Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);

    @Autowired
    public UserController (ISeekingTheLordService iSeekingTheLordService,IEmployeeService iEmployeeService,IShopService iShopService,IUsersService iUsersService,IPetService iPetService,RedisTemplate redisTemplate){
        this.iSeekingTheLordService=iSeekingTheLordService;
        this.iEmployeeService = iEmployeeService;
        this.iShopService = iShopService;
        this.iUsersService = iUsersService;
        this.iPetService=iPetService;
        this.redisTemplate=redisTemplate;

    }

    @PostMapping("/publish")
    public NetResult Publish(@RequestBody AddPetParam addPetParam, HttpServletRequest request){
        String token = request.getHeader("token");
        //判断token有没有
        if (StringUtil.isEmpty(token)){
            return ResultGenerator.genErrorResult(NetCode.TOKEN_NOT_EXIST, Constants.TOKEN_IS_NULL);
        }

        SeekingTheLord seekingTheLord = addPetParam.seekingTheLord;
        Users user = (Users) redisTemplate.opsForValue().get(token);
        //判断token过期没
        if (user==null){
            return ResultGenerator.genErrorResult(NetCode.TOKEN_INVALID, Constants.INVALID_TOKEN);
        }
        //名字不能空
        if (StringUtil.isEmpty(seekingTheLord.getName())){
            return ResultGenerator.genErrorResult(NetCode.PET_NAME_INVALID, Constants.PETNAME_IS_NULL);
        }

        //地址不能空
        if (StringUtil.isEmpty(seekingTheLord.getAddress())){
            return ResultGenerator.genErrorResult(NetCode.ADDRESS_INVALID, Constants.ADDRESS_IS_NULL);
        }

        //判断性别的
        if (StringUtil.isEmpty(seekingTheLord.getSex())||seekingTheLord.getSex().equals("雄")||seekingTheLord.getSex().equals("雌")){
            return ResultGenerator.genErrorResult(NetCode.SEX_INVALID, Constants.SEX_ERROR);
        }

        if (seekingTheLord.getIsInoculation()!=0||seekingTheLord.getIsInoculation()!=1){
            return ResultGenerator.genErrorResult(NetCode.ISINOCULATION_INVALID, Constants.INOCULATION_ERROR);
        }

        if (seekingTheLord.getBirth()<0){
            return  ResultGenerator.genErrorResult(NetCode.BIRTH_INVALID, Constants.BIRTH_ERROR);
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
            //Users users = iUsersService.findById(user_id);
            seekingTheLord.setUsers(user);

            int result =iSeekingTheLordService.add(seekingTheLord);
            if (result!=1){
                return ResultGenerator.genFailResult("添加失败");
            }
//            System.out.println(shop.getId());
//            System.out.println(admin.getId());
//            System.out.println(pet.getId());
//            System.out.println(userMsg.getId());
            int result1 = iSeekingTheLordService.addTask(shop.getId(),admin.getId(),pet.getId(),user.getId(),seekingTheLord.getId());
            if (result1!=1){
                return ResultGenerator.genFailResult("添加失败");
            }
            return ResultGenerator.genSuccessResult(pet);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResultGenerator.genFailResult("添加失败");
    }



    //请求寻主列表
    @GetMapping("/pettype")
    public NetResult PetList(int state){
        if (state == 0){
            List<SeekingTheLord> list = iSeekingTheLordService.getPetListByState(state);
            return ResultGenerator.genSuccessResult(list);
        }else if (state == 1){
            List<SeekingTheLord> list = iSeekingTheLordService.getPetListByState(state);
            return ResultGenerator.genSuccessResult(list);
        }else {
            return ResultGenerator.genErrorResult(NetCode.PET_TYPE_ERROR,Constants.PET_TYPE_ERROR);
        }
    }

    @GetMapping("/userlist")
    public NetResult UserList( HttpServletRequest request){
        String token = request.getHeader("token");
        //判断token有没有
        if (StringUtil.isEmpty(token)){
            return ResultGenerator.genErrorResult(NetCode.TOKEN_NOT_EXIST, Constants.TOKEN_IS_NULL);
        }

        Users user = (Users) redisTemplate.opsForValue().get(token);
        //判断token过期没
        if (user==null){
            return ResultGenerator.genErrorResult(NetCode.TOKEN_INVALID, Constants.INVALID_TOKEN);
        }

        List<SeekingTheLord> list =iSeekingTheLordService.getUserList(user.getId());
        try {
            //判断用户是否有发布过寻主任务
            if(list.get(0)!=null)
                return ResultGenerator.genSuccessResult(list);
        }catch (Exception e){
            return ResultGenerator.genErrorResult(NetCode.USER_LIST_IS_NULL,Constants.USER_LIST_IS_NULL);
        }
        return ResultGenerator.genErrorResult(NetCode.USER_LIST_IS_NULL,Constants.USER_LIST_IS_NULL);
    }

}
