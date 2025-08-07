package org.zlk.mcpixelpicturetool.utils;

import org.zlk.mcpixelpicturetool.type.color.Lab;
import org.zlk.mcpixelpicturetool.type.color.SRGB;

import java.awt.*;
import java.util.Set;

public class ColorUtils {
    public static SRGB hexToRGB(String hex) throws NumberFormatException {
        Color color = Color.decode(hex.startsWith("#") ? hex : "#" + hex);
        return new SRGB(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static Lab RGBToLab(SRGB srgb) {
        return srgb.toXYZ().toLab();
    }

    public static Lab RGBToLab(int r, int g, int b) {
        return RGBToLab(new SRGB(r, g, b));
    }

    public static Lab getCloseColor(Set<Lab> labs, Lab lab) {
        double minDeltaE2000 = Double.MAX_VALUE;
        Lab closeLab = null;
        for (Lab l : labs) {
            double d = deltaE2000(l, lab);
            if (d < minDeltaE2000) {
                minDeltaE2000 = d;
                closeLab = l;
            }
        }
        return closeLab;
        //或许它的性能会很差吧
    }

    public static double deltaE2000(Lab lab1, Lab lab2) {
        // 此处代码来源于AI 但经过数据测试了
        double l1 = lab1.L, a1 = lab1.a, b1 = lab1.b;
        double l2 = lab2.L, a2 = lab2.a, b2 = lab2.b;

        // 参数（符合标准）
        double kl = 1.0, kc = 1.0, kh = 1.0;

        // 计算中间值
        double c1 = Math.sqrt(a1 * a1 + b1 * b1);
        double c2 = Math.sqrt(a2 * a2 + b2 * b2);
        double meanC = (c1 + c2) / 2;
        double g = 0.5 * (1 - Math.sqrt(Math.pow(meanC, 7) / (Math.pow(meanC, 7) + Math.pow(25, 7))));

        double a1p = a1 * (1 + g);
        double a2p = a2 * (1 + g);
        double c1p = Math.sqrt(a1p * a1p + b1 * b1);
        double c2p = Math.sqrt(a2p * a2p + b2 * b2);

        double h1p = Math.toDegrees(Math.atan2(b1, a1p));
        if (h1p < 0) h1p += 360;
        double h2p = Math.toDegrees(Math.atan2(b2, a2p));
        if (h2p < 0) h2p += 360;

        double deltaLp = l2 - l1;
        double deltaCp = c2p - c1p;

        double deltahp;
        if (c1p * c2p == 0) {
            deltahp = 0;
        } else if (Math.abs(h2p - h1p) <= 180) {
            deltahp = h2p - h1p;
        } else if (h2p - h1p > 180) {
            deltahp = h2p - h1p - 360;
        } else {
            deltahp = h2p - h1p + 360;
        }

        double deltaHp = 2 * Math.sqrt(c1p * c2p) * Math.sin(Math.toRadians(deltahp / 2));

        double meanL = (l1 + l2) / 2;
        double meanCp = (c1p + c2p) / 2;

        double meanHp;
        if (c1p * c2p == 0) {
            meanHp = h1p + h2p;
        } else if (Math.abs(h1p - h2p) <= 180) {
            meanHp = (h1p + h2p) / 2;
        } else if (h1p + h2p < 360) {
            meanHp = (h1p + h2p + 360) / 2;
        } else {
            meanHp = (h1p + h2p - 360) / 2;
        }

        double t = 1 - 0.17 * Math.cos(Math.toRadians(meanHp - 30))
                + 0.24 * Math.cos(Math.toRadians(2 * meanHp))
                + 0.32 * Math.cos(Math.toRadians(3 * meanHp + 6))
                - 0.20 * Math.cos(Math.toRadians(4 * meanHp - 63));

        double sl = 1 + (0.015 * Math.pow(meanL - 50, 2)) / Math.sqrt(20 + Math.pow(meanL - 50, 2));
        double sc = 1 + 0.045 * meanCp;
        double sh = 1 + 0.015 * meanCp * t;

        double deltaTheta = 30 * Math.exp(-Math.pow((meanHp - 275) / 25, 2));
        double rc = 2 * Math.sqrt(Math.pow(meanCp, 7) / (Math.pow(meanCp, 7) + Math.pow(25, 7)));
        double rt = -rc * Math.sin(Math.toRadians(2 * deltaTheta));

        // 最终Delta E 2000计算
        return Math.sqrt(
                Math.pow(deltaLp / (kl * sl), 2) +
                        Math.pow(deltaCp / (kc * sc), 2) +
                        Math.pow(deltaHp / (kh * sh), 2) +
                        rt * (deltaCp / (kc * sc)) * (deltaHp / (kh * sh))
        );
    }
}
