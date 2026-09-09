package forum.board.coin;

public enum TransactionType {

    INITIAL,           // 建立錢包
    TRANSFER_IN,       // 轉帳收入
    TRANSFER_OUT,      // 轉帳支出
    DAILY_REWARD,      // 每日獎勵
    PURCHASE,          // 商城消費
    REFUND,
    PURCHASE_REFUND,   // 商城退款
    ADMIN_ADD,         // 管理員加幣
    ADMIN_DEDUCT       // 管理員扣幣    
}