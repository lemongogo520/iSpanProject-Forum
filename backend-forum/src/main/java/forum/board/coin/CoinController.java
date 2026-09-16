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
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.dao.ConcurrencyFailureException;


@Controller
public class CoinController {
    private final CoinService coinService;
    private final DailyTaskService dailyTaskService;

    public CoinController(
            CoinService coinService,
            DailyTaskService dailyTaskService) {

        this.coinService = coinService;
        this.dailyTaskService = dailyTaskService;
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
     * 分頁查詢目前登入會員的交易紀錄。
     */
    @ResponseBody
    @GetMapping("/api/coins/transactions")
    public ResponseEntity<?> getTransactions(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

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

        Page<CoinTransaction> transactionPage =
                coinService.getTransactions(
                    userId,
                    page,
                    size
                );

        List<TransactionResponse> content =
                transactionPage.getContent()
                    .stream()
                    .map(this::toTransactionResponse)
                    .toList();

        TransactionPageResponse response =
                new TransactionPageResponse(
                    content,
                    transactionPage.getNumber(),
                    transactionPage.getSize(),
                    transactionPage.getTotalElements(),
                    transactionPage.getTotalPages(),
                    transactionPage.isFirst(),
                    transactionPage.isLast()
                );

        return ResponseEntity.ok(response);
    }
    
    /**
     * 查詢目前登入會員今天的任務。
     *
     * GET /api/coins/tasks/today
     */
    @ResponseBody
    @GetMapping("/api/coins/tasks/today")
    public ResponseEntity<?> getTodayTasks(
            HttpSession session) {

        UUID userId = getLoginUserId(session);

        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse(
                            false,
                            "請先登入"));
        }

        try {
            List<DailyTaskResponse> tasks =
                    dailyTaskService.getTodayTasks(userId);

            return ResponseEntity.ok(tasks);

        } catch (ConcurrencyFailureException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new MessageResponse(
                            false,
                            "任務資料正在更新，請重新查詢"));

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(
                            false,
                            e.getMessage()));
        }
    }

    /**
     * 手動領取每日任務獎勵。
     *
     * POST /api/coins/tasks/{taskId}/claim
     *
     * 不接受前端指定會員 ID 或獎勵金額。
     */
    @ResponseBody
    @PostMapping("/api/coins/tasks/{taskId}/claim")
    public ResponseEntity<?> claimTaskReward(
            @PathVariable("taskId") UUID taskId,
            HttpSession session) {

        UUID userId = getLoginUserId(session);

        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse(
                            false,
                            "請先登入"));
        }

        try {
            DailyTaskClaimResponse response =
                    dailyTaskService.claimReward(
                            userId,
                            taskId);

            return ResponseEntity.ok(response);

        } catch (ConcurrencyFailureException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new MessageResponse(
                            false,
                            "資料正在更新，請重新查詢任務狀態後再試"));

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(
                            false,
                            e.getMessage()));

        } catch (IllegalStateException e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse(
                            false,
                            "任務或入帳紀錄異常，請聯絡系統管理員"));
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
    
    /**
     * 完成今日簽到。
     * 完成後仍需另外呼叫領獎 API。
     */
    @ResponseBody
    @PostMapping("/api/coins/tasks/check-in")
    public ResponseEntity<?> checkIn(HttpSession session) {

        UUID userId = getLoginUserId(session);

        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse(
                            false,
                            "請先登入"));
        }

        try {
            return ResponseEntity.ok(
                    dailyTaskService.checkIn(userId));

        } catch (ConcurrencyFailureException e) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new MessageResponse(
                            false,
                            "簽到資料正在更新，請重新查詢"));

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(
                            false,
                            e.getMessage()));
        }
    }
    
    
   
    
    //DTO
    
    
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
    
    public record TransactionPageResponse(
            List<TransactionResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last) {
    }
    

    /*
     * 一般訊息回傳格式。
     */
    public record MessageResponse(
            boolean success,
            String message) {
    }
}