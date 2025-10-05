package com.arc_studio.brick_lib_api.core;

/**
 * 符合<a href="https://semver.org/">语义化版本规范（Semantic Versioning 2.0.0）</a>的版本号
 * @author arc_studio
 */
public final class Version implements Comparable<Version> {
    /**
     * 主版本号
     */
    private int major = 0;
    /**
     * 次版本号
     */
    private int minor = 0;
    /**
     * 修订版本号
     */
    private int patch = 0;
    /**
     * 先行版本号
     */
    private String preRelease = "";
    /**
     * 构建版本号
     */
    private String buildMeta = "";

    private Version() {
    }

    public static Version parse(String string) {
        Version version = new Version();
        boolean pre = string.contains("-");
        boolean build = string.contains("+");
        if (pre && build) {
            String[] split = string.split("-");
            String[] split1 = split[1].split("\\+");
            version.preRelease = split1[0];
            version.buildMeta = split1[1];
            String[] split2 = split[0].split("\\.");
            version.major = Integer.parseInt(split2[0]);
            version.minor = Integer.parseInt(split2[1]);
            version.patch = split2.length>=3 ? Integer.parseInt(split2[2]):0;
        } else if (pre) {
            String[] split = string.split("-");
            version.preRelease = split[1];
            String[] split1 = split[0].split("\\.");
            version.major = Integer.parseInt(split1[0]);
            version.minor = Integer.parseInt(split1[1]);
            version.patch = split1.length>=3 ? Integer.parseInt(split1[2]):0;
        } else if (build) {
            String[] split = string.split("\\+");
            version.buildMeta = split[1];
            String[] split1 = split[0].split("\\.");
            version.major = Integer.parseInt(split1[0]);
            version.minor = Integer.parseInt(split1[1]);
            version.patch = split1.length>=3 ? Integer.parseInt(split1[2]):0;
        } else {
            String[] split = string.split("\\.");
            version.major = Integer.parseInt(split[0]);
            version.minor = Integer.parseInt(split[1]);
            version.patch = split.length>=3 ? Integer.parseInt(split[2]):0;
        }
        return version;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(major).append(".").append(minor).append(".").append(patch);
        if (!preRelease.isBlank()) {
            sb.append("-").append(preRelease);
        }
        if (!buildMeta.isBlank()) {
            sb.append("+").append(buildMeta);
        }
        return sb.toString();
    }

    public int minor() {
        return minor;
    }

    public int major() {
        return major;
    }

    public int patch() {
        return patch;
    }

    public String preRelease() {
        return preRelease;
    }

    public String buildMeta() {
        return buildMeta;
    }

    /**
     * @param o the object to be compared.
     */
    @Override
    public int compareTo(Version o) {
        if (this == o) {
            return 0;
        }
        if (this.major != o.major) {
            return Integer.compare(this.major, o.major);
        }
        if (this.minor != o.minor) {
            return Integer.compare(this.minor, o.minor);
        }
        if (this.patch != o.patch) {
            return Integer.compare(this.patch, o.patch);
        }
        PreReleaseType p1 = PreReleaseType.RELEASE;
        PreReleaseType p2 = PreReleaseType.RELEASE;
        if (!this.preRelease.isBlank() && !o.preRelease.isBlank()) {
            if (this.preRelease.equals(o.preRelease)) {
                return 0;
            }
            try {
                p1 = PreReleaseType.valueOf(this.preRelease.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
            try {
                p2 = PreReleaseType.valueOf(o.preRelease.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }else{
            if (!this.preRelease.isBlank()) {
                try {
                    p1 = PreReleaseType.valueOf(this.preRelease.toUpperCase());
                }
                catch (IllegalArgumentException ignored) {
                }
            }
            if (!o.preRelease.isBlank()) {
                try {
                    p2 = PreReleaseType.valueOf(o.preRelease.toUpperCase());
                }catch (IllegalArgumentException ignored) {
                }
            }
        }
        return p1.compare(p2);
    }

    /**
     * 预设的先行版本号枚举
     */
    public enum PreReleaseType {
        /**
         * Base版本是在软件开发过程中选定的一个固定点，用于作为后续开发、测试和部署的参考
         */
        BASE("base", 0),
        /**
         * 内测版，不对外发布。bug会比较多，功能也不全
         */
        ALPHA("alpha", 1),
        /**
         * 公测版，该版本仍然存在很多bug，但比alpha版本稳定一些。这个阶段版本还会不断增加新功能
         */
        BETA("beta", 2),
        /**
         * 最终测试版本，发行候选版本，基本不再加入新的功能，主要修复bug
         */
        RC("rc", 3),
        /**
         * 正式发布版，官方推荐使用的版本
         */
        RELEASE("release", 4),
        /**
         * 最终版，表示该软件已经完成并准备好供公众使用
         */
        FINAL("final", 4),
        /**
         * 稳定版，通过了广泛测试并且被认为是足够稳定的，适合一般用户广泛使用的版本。这个版本相比于测试版（如Alpha或Beta版本）更加稳定，已经修复了许多已知的bug，并且完善了一些功能。
         */
        STABLE("stable", 4),
        /**
         * 正式发布的版本，官方开始推荐广泛使用，Release版本的另一种表示方法
         */
        GA("ga", 4),
        /**
         * Snapshot版本表示正在开发中的不稳定版本。通常用于开发阶段，表示尚未准备好发布或测试的代码。此版本允许开发者在开发过程中共享和更新代码，而不必等待其他人完成他们的更改。
         */
        SNAPSHOT("snapshot", 2),
        /**
         * 长期支持版本，这被认为是最稳定的版本，经过了广泛的测试，并且主要包含多年的改进。此版本不一定涉及功能更新，除非有更新的 LTS 版本，但是可以获取必要的错误和安全修复
         */
        LTS("lts", 4),
        /**
         * 包含基础功能和内容的版本，通常不包括高级功能或附加内容
         */
        STANDARD("standard", 4),
        /**
         * 提供了最全面的功能集合，旨在满足用户所有可能的需求。通常包含高级工具、深度定制选项以及额外的安全特性等
         */
        ULTIMATE("ultimate", 4),
        /**
         * 试用版，通常用于付费软件，用户可以在购买正式版本之前免费试用，以了解其功能和使用体验。
         */
        TRIAL("trial", 4),
        /**
         * 精简版，可能会削减一些高级功能或特性。某些复杂功能可能被简化或移除，以提供更基本的功能
         */
        LITE("lite", 4),
        /**
         * 加强版，通常指的是在原版基础上增加了一些功能或性能
         */
        PLUS("plus", 4),
        /**
         * 自由版，指软件的免费版本，通常功能有限，但可以无限期免费使用。
         */
        FREE("free", 4),
        /**
         * 通常指的是软件的预览版或测试版，主要用于开发者和测试人员在正式发布之前进行测试和反馈
         */
        PREVIEW("preview", 1),
        /**
         * 通常是有偿提供的，与基础的免费版本有所区别。包含额外的先进功能和独家特性
         */
        PREMIUM("premium", 4),
        /**
         * 提供了一套专业的功能，适用于大多数日常工作和业务需求。功能相对较为全面，但可能不包含Ultimate中的某些高级或特定用途的工具，但仍然能够满足大部分用户的常规使用需求。
         */
        PROFESSIONAL("professional", 4),
        /**
         * 软件的简化和快速版本，通常精简了一些功能，以便于用户操作和快速部署
         */
        EXPRESS("express", 4),
        /**
         * 未注册版，可注册或购买成为正式版
         */
        UNREGISTERED("unregistered", 4),
        /**
         * 演示版，仅仅集成了正式版中的几个功能，不能升级成正式版 ，一般会有功能限制
         */
        DEMO("demo", 4);
        final String name;
        final int ordinal;

        PreReleaseType(String name, int ordinal) {
            this.name = name;
            this.ordinal = ordinal;
        }

        public int compare(PreReleaseType other){
            return Integer.compare(this.ordinal, other.ordinal);
        }
    }

    /**
     * 版本号构建器，用于快速创建版本号
     */
    public static class Builder {
        private final int major;
        private final int minor;
        private final int patch;
        private String preRelease = "";
        private String buildMeta = "";

        /**
         * 获取一个版本号构建器
         * <p>
         * 版本号格式为：{@code major.minor.patch[-preRelease][+buildMeta]}
         * </p>
         * <p>
         * 其中，{@code major}、{@code minor}、{@code patch}为必须指定的版本号，{@code preRelease}和{@code buildMeta}为可选指定的版本号
         * </p>
         * <p>
         * @param major 主版本号
         * @param minor 次版本号
         * @param patch 修订版本号
         */
        public Builder(int major, int minor, int patch) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
        }

        public static Builder create(int major, int minor, int patch){
            return new Builder(major,minor,patch);
        }

        /**
         * 快速设置先行版本号为{@link PreReleaseType#ALPHA}
         */
        public Builder alpha() {
            this.preRelease = PreReleaseType.ALPHA.name;
            return this;
        }

        /**
         * 快速设置先行版本号为{@link PreReleaseType#BETA}
         */
        public Builder beta() {
            this.preRelease = PreReleaseType.BETA.name;
            return this;
        }

        /**
         * 快速设置先行版本号为{@link PreReleaseType#RC}
         */
        public Builder rc() {
            this.preRelease = PreReleaseType.RC.name;
            return this;
        }

        /**
         * 快速设置先行版本号为{@link PreReleaseType#RELEASE}
         */
        public Builder release() {
            this.preRelease = PreReleaseType.RELEASE.name;
            return this;
        }

        /**
         * 设置先行版本号为枚举值
         */
        public Builder preRelease(PreReleaseType preReleaseType) {
            this.preRelease = preReleaseType.name;
            return this;
        }

        /**
         * 设置先行版本号为枚举+指定数字后缀
         */
        public Builder preRelease(PreReleaseType preReleaseType, int suffix) {
            this.preRelease = preReleaseType.name + "." + suffix;
            return this;
        }

        /**
         * 设置先行版本号为枚举+指定字符串后缀
         */
        public Builder preRelease(PreReleaseType preReleaseType, String suffix) {
            this.preRelease = preReleaseType.name + "." + suffix;
            return this;
        }

        /**
         * 设置先行版本号为任意字符串
         */
        public Builder preRelease(String preRelease) {
            this.preRelease = preRelease;
            return this;
        }

        /**
         * 设置构建版本号
         */
        public Builder buildMeta(String buildMeta) {
            this.buildMeta = buildMeta;
            return this;
        }

        /**
         * 获取生成的版本号
         */
        public Version build() {
            Version version = new Version();
            version.major = this.major;
            version.minor = this.minor;
            version.patch = this.patch;
            version.preRelease = this.preRelease;
            version.buildMeta = this.buildMeta;
            return version;
        }
    }
}