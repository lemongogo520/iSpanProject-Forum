package forum.board.coin;

public enum TransactionType {

    INITIAL,           // 建立錢包

    TRANSFER_IN,       // 轉帳收入
    TRANSFER_OUT,      // 轉帳支出

    DAILY_TASK_REWARD,  // 每日任務完成後，手動領取獎勵

    PURCHASE,          // 商城消費
    REFUND,            // 既有商城退款類型
    PURCHASE_REFUND,   // 既有商城退款類型

    // 保留以下類型，供舊交易紀錄讀取。
    // 新的每日任務流程不再使用。
    DAILY_REWARD,      // 舊版每日獎勵
    ADMIN_ADD,         // 舊版管理員加點
    ADMIN_DEDUCT       // 舊版管理員扣點
}