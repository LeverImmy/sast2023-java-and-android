package de.mide.pegsolitaire.model;

/**
 * 格子的状态
 */
public enum PlaceStatusEnum {

    /** 占位 */
    PLACEHOLDER,

    /** 不可访问 */
    BLOCKED,

    /** 无棋子 */
    SPACE,

    /** 有棋子 */
    PEG
}
