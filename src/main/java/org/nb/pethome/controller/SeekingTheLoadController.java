package org.nb.pethome.controller;
import org.nb.pethome.common.Constants;
import org.nb.pethome.entity.Employee;
import org.nb.pethome.entity.Shop;
import org.nb.pethome.entity.Users;
import org.nb.pethome.net.NetCode;
import org.nb.pethome.net.NetResult;
import org.nb.pethome.net.param.SeekingTheLordParam;
import org.nb.pethome.service.ISeekingTheLordService;
import org.nb.pethome.service.IShopService;
import org.nb.pethome.utils.ResultGenerator;
import org.nb.pethome.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
public class SeekingTheLoadController {
    private RedisTemplate redisTemplate;
    private ISeekingTheLordService iSeekingTheLordService;
    private IShopService iShopService;


    @Autowired
    public SeekingTheLoadController(RedisTemplate redisTemplate,ISeekingTheLordService iSeekingTheLordService,IShopService iShopService) {
        this.redisTemplate = redisTemplate;
        this.iSeekingTheLordService=iSeekingTheLordService;
        this.iShopService=iShopService;

    }


    /*通过用户的id去查询寻主信息*/
    @GetMapping("/getauditlist")
    public NetResult getAuditList(HttpServletRequest request){
        //从请求头拿到token
        String token = request.getHeader("token");
        System.out.println(token);
        //判断token是否存在
        if (StringUtil.isEmpty(token)) {
            return ResultGenerator.genErrorResult(NetCode.TOKEN_NOT_EXIST, "token不存在");
        }
        //根据token拿到用户的数据
        Users user = (Users) redisTemplate.opsForValue().get(token);
        //判断token是否过期
        if (user == null) {
            return ResultGenerator.genErrorResult(NetCode.TOKEN_INVALID, Constants.INVALID_TOKEN);
        }
        //获取已审核的寻主列表
        List<SeekingTheLordParam> list=iSeekingTheLordService.getAuditList(user.getId());
        return ResultGenerator.genSuccessResult(list);
    }

    /*通过商铺的id去查询已审核，未审核的寻主信息*/
    @GetMapping("/getauditlistbyshop")
    public NetResult getAuditListByShop(HttpServletRequest request){
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
        //通过管理员找到商铺
        Shop shop=iShopService.findByAdmin(employee.getId());
        //获取已审核未审核的寻主列表
        List<SeekingTheLordParam> list=iSeekingTheLordService.getAuditListByShop(shop.getAdmin_id());
        return ResultGenerator.genSuccessResult(list);
    }

    //审核寻主任务
    @PostMapping("/auditseekingthelord")
    public NetResult auditSeekingTheLord(@RequestParam long id,HttpServletRequest request) {
        //从请求头拿到token
        String token = request.getHeader("token");
        System.out.println(token);
        //判断token是否存在
        if (StringUtil.isEmpty(token)) {
            return ResultGenerator.genErrorResult(NetCode.TOKEN_NOT_EXIST, "token不存在");
        }
        //根据token拿到管理员的数据
        Employee employee = (Employee) redisTemplate.opsForValue().get(token);
        //判断token是否过期
        if (employee == null) {
            return ResultGenerator.genErrorResult(NetCode.TOKEN_INVALID, Constants.INVALID_TOKEN);
        }
        //审核寻主
        int count=iSeekingTheLordService.auditSeekingTheLord(id);
        if (count!=1){
            return ResultGenerator.genSuccessResult("审核失败");
        }
        return ResultGenerator.genSuccessResult("审核成功");
    }

    //商铺列表（用户端）
    @GetMapping("/shoplist")
    public NetResult shopList(HttpServletRequest request) {
        //从请求头拿到token
        String token = request.getHeader("token");
        //判断token是否存在
        System.out.println(token);
        if (StringUtil.isEmpty(token)) {
            return ResultGenerator.genErrorResult(NetCode.TOKEN_NOT_EXIST, "token不存在");
        }
        Users user = (Users) redisTemplate.opsForValue().get(token);
        //判断token是否过期
        if (user == null) {
            return ResultGenerator.genErrorResult(NetCode.TOKEN_INVALID, Constants.INVALID_TOKEN);
        }
        //查看商铺列表
        List<Shop> list =iShopService.list();
        return ResultGenerator.genSuccessResult(list);
    }


}
