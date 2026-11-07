package org.faketale.mnemic_odyssey.compendium;

/** 图鉴的四个门类，顺序即界面标签顺序。 */
public enum CompendiumCategory {
    BLOCK("block", true),
    MOB("mob", true),
    ITEM("item", false),
    BOSS("boss", true);

    public final String key;
    /** 是否拥有记忆等级 */
    public final boolean leveled;

    CompendiumCategory(String key, boolean leveled) {
        this.key = key;
        this.leveled = leveled;
    }

    public static CompendiumCategory byId(int id) {
        CompendiumCategory[] v = values();
        return id >= 0 && id < v.length ? v[id] : BLOCK;
    }
}
