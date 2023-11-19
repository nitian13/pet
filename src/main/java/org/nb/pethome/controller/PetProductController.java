package org.nb.pethome.controller;
import org.nb.pethome.common.Constants;
import org.nb.pethome.entity.Employee;
import org.nb.pethome.entity.PetProduct;
import org.nb.pethome.net.NetCode;
import org.nb.pethome.net.NetResult;
import org.nb.pethome.net.param.SeekingTheLordParam;
import org.nb.pethome.service.IPetProductService;
import org.nb.pethome.utils.ResultGenerator;
import org.nb.pethome.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
public class PetProductController {
    private IPetProductService iPetProductService;
    private RedisTemplate redisTemplate;

    @Autowired
    public PetProductController(IPetProductService iPetProductService,RedisTemplate redisTemplate){
        this.iPetProductService=iPetProductService;
        this.redisTemplate=redisTemplate;
    }

    //添加宠物商品
    @PostMapping("/addpetproduct")
    public NetResult addPetProduct(@RequestBody PetProduct petProduct){

        //售价判断
        if (petProduct.getPetSellPrice()<0){
            return ResultGenerator.genFailResult("售价不能小于0");
        }
        //售价
        double sell=petProduct.getPetSellPrice();
        //获取寻主的id
        long id=petProduct.getSeekingthelord_id();
        //根据id找到寻主的数据
        SeekingTheLordParam seekingTheLordParam=iPetProductService.getSeekingTheLord(id);
        if (seekingTheLordParam==null){
            return ResultGenerator.genFailResult("寻主异常");
        }
        //添加数据
        petProduct.setPetName(seekingTheLordParam.getName());
        petProduct.setPetSex(seekingTheLordParam.getSex());
        petProduct.setIsInoculation(seekingTheLordParam.getIsInoculation());
        petProduct.setPetBirth(seekingTheLordParam.getBirth());
        petProduct.setPetSellPrice(sell);
        petProduct.setUser_id(seekingTheLordParam.getUser_id());
        petProduct.setAdmin_id(seekingTheLordParam.getAdmin_id());
        petProduct.setPet_id(seekingTheLordParam.getPet_id());
        petProduct.setShop_id(seekingTheLordParam.getShop_id());
        //添加宠物
        int num=iPetProductService.add(petProduct);
        if (num!=1){
            return ResultGenerator.genFailResult("添加失败");
        }
        return ResultGenerator.genSuccessResult(petProduct);
    }

    //宠物商品列表
    @GetMapping("/petproductlist")
    public NetResult petProductList(HttpServletRequest request){
        //从请求头拿到token
        String token = request.getHeader("token");
        System.out.println(token);
        //判断token是否存在
        if (StringUtil.isEmpty(token)) {
            return ResultGenerator.genErrorResult(NetCode.TOKEN_NOT_EXIST, "token不存在");
        }
        //根据token拿到管理员的数据
        Employee employee= (Employee) redisTemplate.opsForValue().get(token);
        //判断token是否过期
        if (employee == null) {
            return ResultGenerator.genErrorResult(NetCode.TOKEN_INVALID, Constants.INVALID_TOKEN);
        }

        //查询宠物列表
        List<PetProduct> list=iPetProductService.getPetProduct(employee.getId());
        return ResultGenerator.genSuccessResult("商品上架成功");
    }




    //上架
    @PostMapping("/petshelves")
    public NetResult petShelves(@RequestParam long id){
        //通过id去找商品
        PetProduct petProduct=iPetProductService.getPetProductById(id);
        //设置上架时间
        petProduct.setSellTime(System.currentTimeMillis());
        //修改状态为上架
        int number=iPetProductService.petShelves(id,petProduct.getSellTime());

        if (number!=1){
            return ResultGenerator.genFailResult("商品上架失败");
        }
        return ResultGenerator.genSuccessResult("商品上架成功");
    }

    //下架
    @PostMapping("/petnotshelves")
    public NetResult petNotShelves(@RequestParam long id){
        //通过id去找商品
        PetProduct petProduct=iPetProductService.getPetProductById(id);
        //设置下架时间
        petProduct.setEndTime(System.currentTimeMillis());
        //修改状态为上架
        int number=iPetProductService.petNotShelves(id,petProduct.getEndTime());
        if (number!=1){
            return ResultGenerator.genFailResult("商品下失败");
        }
        return ResultGenerator.genSuccessResult("商品下架成功");
    }

    //用户买宠物,改变商品的状态为已出售
    @PostMapping("/petproductsell")
    public NetResult petProductSell(@RequestParam long id){

      int num=iPetProductService.petSell(id);
      if (num!=1){
          return ResultGenerator.genFailResult("购买失败");
      }
        return ResultGenerator.genSuccessResult("购买成功");
    }

}
