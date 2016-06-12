package com.jifenke.lepluslive.merchant.controller;

import com.jifenke.lepluslive.global.util.LejiaResult;
import com.jifenke.lepluslive.global.util.MvUtil;
import com.jifenke.lepluslive.global.util.PaginationUtil;
import com.jifenke.lepluslive.merchant.domain.entities.Area;
import com.jifenke.lepluslive.merchant.domain.entities.City;
import com.jifenke.lepluslive.merchant.service.CityService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;

import java.util.List;

/**
 * Created by wcg on 16/3/17.
 */
@RestController
public class CityController {

  @Inject
  private CityService cityService;

  @RequestMapping(value = "/city", method = RequestMethod.GET)
  public ModelAndView goShowCityPage(@RequestParam(value = "page", required = false) Integer offset,
                                     @RequestParam(value = "per_page", required = false) Integer limit,
                                     Model model) {
    model.addAttribute("cities", cityService
        .findCitiesByPage(PaginationUtil.generatePageRequest(offset, limit)));
    return MvUtil.go("/merchant/cityList");
  }

  @RequestMapping(value = "/city/edit", method = RequestMethod.GET)
  public ModelAndView goEditCityPage(@RequestParam(value = "id", required = false) Long id,
                                     Model model) {
    if (id != null) {
      model.addAttribute("city", cityService.findCityById(id));
    }
    return MvUtil.go("/merchant/cityEdit");
  }

  @RequestMapping(value = "/city", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public LejiaResult createCity(@RequestBody City city) {
    cityService.createCity(city);
    return LejiaResult.ok("创建城市成功");
  }

  @RequestMapping(value = "/city", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
  public LejiaResult editCity(@RequestBody City city) {
    cityService.editCity(city);
    return LejiaResult.ok("修改城市成功");
  }

  @RequestMapping(value = "/city/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
  public LejiaResult deleteCity(@PathVariable Long id) {
    cityService.deleteCity(id);
    return LejiaResult.ok("删除城市成功");
  }

  @ApiOperation(value = "获取城市列表")
  @RequestMapping(value = "/city/list", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult list(@ApiParam(value = "第几页") @RequestParam(required = false) Integer page) {
    List<City> cities = cityService.findCitiesByPage(PaginationUtil.generatePageRequest(page, 10));
    return LejiaResult.build(200, "ok", cities);
  }

  @ApiOperation(value = "获取某个城市的地区列表")
  @RequestMapping(value = "/city/areas", method = RequestMethod.POST)
  public
  @ResponseBody
  LejiaResult areas(@ApiParam(value = "城市Id") @RequestParam(required = false) Long id) {
    List<Area> areaList = cityService.findAreaListByCity(id);
    return LejiaResult.build(200, "ok", areaList);
  }

}
