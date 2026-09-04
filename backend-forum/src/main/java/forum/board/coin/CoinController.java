package forum.board.coin;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;

@Controller
public class CoinController {

    private final CoinService coinService;

    public CoinController(CoinService coinService) {
        this.coinService = coinService;
    }

    /*
     * 顯示錢包 HTML 頁面。
     */
    @GetMapping("/coin/wallet")
    public String walletPage(HttpSession session) {

        if (getLoginUserId(session) == null) {
            return "redirect:/login";
        }

        return "coin/wallet";
    }

    /*
     * 顯示交易紀錄 HTML 頁面。
     */
    @GetMapping("/coin/transactions")
    public String transactionsPage(HttpSession session) {

        if (getLoginUserId(session) == null) {
            return "redirect:/login";
        }

        return "coin/transactions";
    }

    /*
     * 查詢目前登入會員的錢包。
     */
    @ResponseBody
    @GetMapping("/api/coins/wallet")
    public ResponseEntity<?> getWallet(HttpSession session) {

        UUID userId = getLoginUserId(session);

        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse(
                        false,
                        "請先登入"
                    ));
        }

        CoinWallet wallet =
                coinService.getOrCreateWallet(userId);

        WalletResponse response = new WalletResponse(
                wallet.getId(),
                wallet.getBalance()
        );

        return ResponseEntity.ok(response);
    }

    /*
     * 查詢目前登入會員的交易紀錄。
     */
    @ResponseBody
    @GetMapping("/api/coins/transactions")
    public ResponseEntity<?> getTransactions(
            HttpSession session) {

        UUID userId = getLoginUserId(session);

        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse(
                        false,
                        "請先登入"
                    ));
        }

        List<TransactionResponse> response =
                coinService.getTransactions(userId)
                    .stream()
                    .map(this::toTransactionResponse)
                    .toList();

        return ResponseEntity.ok(response);
    }
    
    /*
     * 管理員替指定會員加幣或扣幣。
     */
    @ResponseBody
    @PostMapping("/api/admin/coins/adjust")
    public ResponseEntity<MessageResponse> adjustCoins(
            @RequestBody CoinAdjustRequest request,
            HttpSession session) {

        UUID adminUserId = getLoginUserId(session);

        /*
         * 尚未登入。
         */
        if (adminUserId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse(false,"請先登入"));
        }

        try {
            Long balanceAfter =
                    coinService.adjustCoins(
                        adminUserId,
                        request
                    );

            return ResponseEntity.ok(
                new MessageResponse(
                    true,
                    "調整成功，目前餘額為 " + balanceAfter + " 枚"));

        } catch (SecurityException e) {

            /*
             * 已登入，但不是 systemadmin。
             */
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse(
                        false,
                        e.getMessage()
                    ));

        } catch (IllegalArgumentException e) {

            /*
             * 帳號、金額或操作內容錯誤。
             */
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(
                        false,
                        e.getMessage()
                    ));

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse(
                        false,
                        "調整失敗，請稍後再試"
                    ));
        }
    }
    
    

    /*
     * 領取每日獎勵。
     */
    @ResponseBody
    @PostMapping("/api/coins/daily-reward")
    public ResponseEntity<MessageResponse> claimDailyReward(
            HttpSession session) {

        UUID userId = getLoginUserId(session);

        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse(
                        false,
                        "請先登入"
                    ));
        }

        try {
            Long rewardAmount =
                    coinService.claimDailyReward(userId);

            return ResponseEntity.ok(
                new MessageResponse(
                    true,
                    "成功領取每日獎勵 "
                        + rewardAmount
                        + " 枚"
                )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(
                        false,
                        e.getMessage()
                    ));

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse(
                        false,
                        "領取失敗，請稍後再試"
                    ));
        }
    }

    /*
     * 執行會員轉帳。
     */
    @ResponseBody
    @PostMapping("/api/coins/transfer")
    public ResponseEntity<MessageResponse> transfer(
            @RequestBody TransferRequest request,
            HttpSession session) {

        UUID userId = getLoginUserId(session);

        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse(
                        false,
                        "請先登入"
                    ));
        }

        try {
            coinService.transfer(userId, request);

            return ResponseEntity.ok(
                new MessageResponse(
                    true,
                    "轉帳成功"
                )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(
                        false,
                        e.getMessage()
                    ));

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse(
                        false,
                        "轉帳失敗，請稍後再試"
                    ));
        }
    }

    /*
     * 從 Session 取得目前登入會員 ID。
     */
    private UUID getLoginUserId(HttpSession session) {

        Object loginUserId =
                session.getAttribute("loginUserId");

        if (loginUserId instanceof UUID uuid) {
            return uuid;
        }

        if (loginUserId instanceof String id) {
            try {
                return UUID.fromString(id);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        return null;
    }

    /*
     * 將 Entity 轉成前端需要的資料。
     */
    private TransactionResponse toTransactionResponse(
            CoinTransaction transaction) {

        return new TransactionResponse(
                transaction.getId(),
                transaction.getTransactionType().name(),
                transaction.getAmount(),
                transaction.getBalanceAfter(),
                transaction.getReferenceId(),
                transaction.getRelatedUserId(),
                transaction.getDescription(),
                transaction.getCreateTime()
        );
    }

    /*
     * 錢包 API 回傳格式。
     */
    public record WalletResponse(
            UUID walletId,
            Long balance) {
    }

    /*
     * 交易紀錄 API 回傳格式。
     */
    public record TransactionResponse(
            UUID id,
            String transactionType,
            Long amount,
            Long balanceAfter,
            UUID referenceId,
            UUID relatedUserId,
            String description,
            Instant createTime) {
    }

    /*
     * 一般訊息回傳格式。
     */
    public record MessageResponse(
            boolean success,
            String message) {
    }
}