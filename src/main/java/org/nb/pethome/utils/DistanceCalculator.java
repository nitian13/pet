package org.nb.pethome.utils;

import org.nb.pethome.entity.Location;

public class DistanceCalculator {

    /**
     * 计算两个位置的距离的工具
     * @param lat1
     * @param lat2
     * @return
     */
    public static double calculateDistance(Location lat1, Location lat2) {
        // 地球半径（单位：千米）
        final int R = 6371;

        // 将经纬度转换为弧度
        double latDistance = Math.toRadians(lat2.getLatitude()-lat1.getLatitude());
        double lonDistance = Math.toRadians(lat2.getLongitude()-lat1.getLongitude());

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1.getLatitude())) * Math.cos(Math.toRadians(lat2.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 计算距离
        return R * c;
    }
}
