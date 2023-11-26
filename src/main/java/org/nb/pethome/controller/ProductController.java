package org.nb.pethome.controller;

import org.nb.pethome.common.Constants;
import org.nb.pethome.entity.Employee;
import org.nb.pethome.entity.Product;
import org.nb.pethome.entity.Users;
import org.nb.pethome.net.NetCode;
import org.nb.pethome.net.NetResult;
import org.nb.pethome.net.param.ProductParam;
import org.nb.pethome.service.IProductService;
import org.nb.pethome.utils.ResultGenerator;
import org.nb.pethome.utils.StringUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class ProductController {

    private IProductService iProductService;
    private RedisTemplate redisTemplate;

    public ProductController(IProductService iProductService,RedisTemplate redisTemplate){
        this.iProductService=iProductService;
        this.redisTemplate=redisTemplate;
    }


    //添加服务商品
    @PostMapping("/addproduct")
    public NetResult addProduct(@RequestBody Product product, HttpServletRequest request){

        //从请求头拿到token
        String token = request.getHeader("token");
        System.out.println(token);
        //判断token是否存在
        if (StringUtil.isEmpty(token)) {
            return ResultGenerator.genErrorResult(NetCode.TOKEN_NOT_EXIST, "token不存在");
        }
        //根据token拿到用户的数据
        Employee employee = (Employee) redisTemplate.opsForValue().get(token);
        //判断token是否过期
        if (employee == null) {
            return ResultGenerator.genErrorResult(NetCode.TOKEN_INVALID, Constants.INVALID_TOKEN);
        }

        //售价判断
        if (product.getSalePrice()<0){
            return ResultGenerator.genFailResult("售价不能小于0");
        }

        //成本价
        if (product.getCostPrice()<0){
            return ResultGenerator.genFailResult("成本价不能小于0");
        }

        //产品名字不能为空
        if (StringUtil.isEmpty(product.getName())){
            return ResultGenerator.genFailResult("产品名字不能为空");
        }
        //设置创建时间
        product.setCreateTime(System.currentTimeMillis());
        //添加服务
        int count=iProductService.add(product);
        if (count!=1){
            return ResultGenerator.genFailResult("添加失败");
        }
            return ResultGenerator.genSuccessResult("添加成功");
    }


    //修改服务商品
    @PostMapping("/updateproduct")
    public NetResult updateProduct(@RequestBody Product product,HttpServletRequest request){
        //从请求头拿到token
        String token = request.getHeader("token");
        System.out.println(token);
        //判断token是否存在
        if (StringUtil.isEmpty(token)) {
            return ResultGenerator.genErrorResult(NetCode.TOKEN_NOT_EXIST, "token不存在");
        }
        //根据token拿到用户的数据
        Employee employee = (Employee) redisTemplate.opsForValue().get(token);
        //判断token是否过期
        if (employee == null) {
            return ResultGenerator.genErrorResult(NetCode.TOKEN_INVALID, Constants.INVALID_TOKEN);
        }

        //售价
        if (product.getSalePrice()<0){
            return ResultGenerator.genFailResult("售价不能小于0");
        }

        //成本价
        if (product.getCostPrice()<0){
            return ResultGenerator.genFailResult("成本价不能小于0");
        }

        //修改服务
        int count=iProductService.update(product);
        if (count!=1){
            return ResultGenerator.genFailResult("修改失败");
        }
        return ResultGenerator.genSuccessResult("修改成功");
    }


    //删除商品
    @PostMapping("/deleteproduct")
    public NetResult deleteProduct(@RequestParam Long id,HttpServletRequest request){

        //从请求头拿到token
        String token = request.getHeader("token");
        System.out.println(token);
        //判断token是否存在
        if (StringUtil.isEmpty(token)) {
            return ResultGenerator.genErrorResult(NetCode.TOKEN_NOT_EXIST, "token不存在");
        }
        //根据token拿到用户的数据
        Employee employee = (Employee) redisTemplate.opsForValue().get(token);
        //判断token是否过期
        if (employee == null) {
            return ResultGenerator.genErrorResult(NetCode.TOKEN_INVALID, Constants.INVALID_TOKEN);
        }
        //删除服务
        int count=iProductService.delete(id);
        if (count!=1){
            return ResultGenerator.genFailResult("删除失败");
        }
        return ResultGenerator.genSuccessResult("删除成功");
    }

    //商品上架
    @PostMapping("/shelvesproduct")
    public NetResult shelvesProduct(@RequestParam Long id,HttpServletRequest request){

        //从请求头拿到token
        String token = request.getHeader("token");
        System.out.println(token);
        //判断token是否存在
        if (StringUtil.isEmpty(token)) {
            return ResultGenerator.genErrorResult(NetCode.TOKEN_NOT_EXIST, "token不存在");
        }
        //根据token拿到用户的数据
        Employee employee = (Employee) redisTemplate.opsForValue().get(token);
        //判断token是否过期
        if (employee == null) {
            return ResultGenerator.genErrorResult(NetCode.TOKEN_INVALID, Constants.INVALID_TOKEN);
        }
        //上架服务
        int count=iProductService.shelves(id);
        if (count!=1){
            return ResultGenerator.genFailResult("上架失败");
        }
        return ResultGenerator.genSuccessResult("上架成功");
    }

    //商品下架
    @PostMapping("/takenoffproduct")
    public NetResult takenOffProduct(@RequestParam Long id,HttpServletRequest request){

        //从请求头拿到token
        String token = request.getHeader("token");
        System.out.println(token);
        //判断token是否存在
        if (StringUtil.isEmpty(token)) {
            return ResultGenerator.genErrorResult(NetCode.TOKEN_NOT_EXIST, "token不存在");
        }
        //根据token拿到用户的数据
        Employee employee = (Employee) redisTemplate.opsForValue().get(token);
        //判断token是否过期
        if (employee == null) {
            return ResultGenerator.genErrorResult(NetCode.TOKEN_INVALID, Constants.INVALID_TOKEN);
        }
        //下架服务
        int count=iProductService.takenOff(id);
        if (count!=1){
            return ResultGenerator.genFailResult("下架失败");
        }
        return ResultGenerator.genSuccessResult("下架成功");
    }


    //商品列表
    @GetMapping("/list")
    public NetResult list(@RequestParam(value="offset",required = false, defaultValue ="1") int offset,
                          @RequestParam(value="pageSize",required = false, defaultValue ="10") int pageSize){

        int i = (offset - 1) * pageSize;
        int total=iProductService.count();
        ProductParam productParam=new ProductParam();
        productParam.setTotal(total);
        List<Product> list = iProductService.List(i,pageSize);
        productParam.setProductList(list);
        return ResultGenerator.genSuccessResult(productParam);
    }


    //用户购买商品
    @PostMapping("/buyproduct")
    public NetResult buyProduct(@RequestParam Long id,HttpServletRequest request){

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
        //通过id去找商品
        Product product=iProductService.getProductById(id);
        if (product==null){
            return ResultGenerator.genFailResult("商品不存在");
        }
        //获取数量
        int count=product.getSaleCount();
        //判断数量
        if (count<1){
            return ResultGenerator.genFailResult("商品已售罄");
        }
        //买商品
        int number=iProductService.buyProduct(id);
        if (number!=1){
            return ResultGenerator.genFailResult("购买失败");
        }
            return ResultGenerator.genFailResult("购买成功");
    }

}
