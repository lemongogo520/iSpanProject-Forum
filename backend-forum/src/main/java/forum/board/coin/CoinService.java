package forum.board.coin;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import forum.board.users.Users;
import forum.board.users.UsersRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class CoinService {
	
	
	private static final long DAILY_REWARD_AMOUNT = 50L;
	private static final ZoneId TAIWAN_ZONE =ZoneId.of("Asia/Taipei");
	private static final String SYSTEM_ADMIN_PERMISSION ="systemadmin";

    private final CoinWalletRepository walletRepository;
    private final CoinTransactionRepository transactionRepository;
    private final UsersRepository usersRepository;
    private final CoinUserPermissionRepository userPermissionRepository;

    

    public CoinService(CoinWalletRepository walletRepository, CoinTransactionRepository transactionRepository,
			UsersRepository usersRepository, CoinUserPermissionRepository userPermissionRepository) {
		super();
		this.walletRepository = walletRepository;
		this.transactionRepository = transactionRepository;
		this.usersRepository = usersRepository;
		this.userPermissionRepository = (CoinUserPermissionRepository) userPermissionRepository;
	}

	/**
     * 查詢錢包；如果會員還沒有錢包，就自動建立。
     */
    @Transactional
    public CoinWallet getOrCreateWallet(UUID userId) {

        return walletRepository.findByUserId(userId)
                .orElseGet(() -> createWallet(userId));
    }

    /**
     * 建立會員錢包。
     */
    private CoinWallet createWallet(UUID userId) {

        Users user = usersRepository.findById(userId)
                .orElseThrow(() ->
                    new IllegalArgumentException("找不到指定會員")
                );

        CoinWallet wallet = new CoinWallet();
        wallet.setUser(user);
        wallet.setBalance(0L);

        return walletRepository.save(wallet);
    }

    /**
     * 查詢會員目前餘額。
     */
    @Transactional
    public Long getBalance(UUID userId) {

        CoinWallet wallet = getOrCreateWallet(userId);

        return wallet.getBalance();
    }

    /**
     * 查詢會員的全部交易紀錄。
     */
    @Transactional
    public List<CoinTransaction> getTransactions(UUID userId) {

        CoinWallet wallet = getOrCreateWallet(userId);

        return transactionRepository
                .findByWalletIdOrderByCreateTimeDesc(wallet.getId());
    }

    /**
     * 會員轉帳。
     */
    @Transactional
    public void transfer(
            UUID senderUserId,
            TransferRequest request) {

        validateTransferRequest(request);

        Users sender = usersRepository.findById(senderUserId)
                .orElseThrow(() ->
                    new IllegalArgumentException("找不到轉帳會員")
                );

        Users recipient = usersRepository
                .findByAccount(request.getRecipientAccount().trim())
                .orElseThrow(() ->
                    new IllegalArgumentException("找不到收款帳號")
                );

        if (sender.getId().equals(recipient.getId())) {
            throw new IllegalArgumentException("不能轉帳給自己");
        }

        CoinWallet senderWallet =
                getOrCreateWallet(sender.getId());

        CoinWallet recipientWallet =
                getOrCreateWallet(recipient.getId());

        long amount = request.getAmount();

        if (senderWallet.getBalance() < amount) {
            throw new IllegalArgumentException("錢包餘額不足");
        }

        long senderBalanceAfter =
                senderWallet.getBalance() - amount;

        long recipientBalanceAfter =
                recipientWallet.getBalance() + amount;

        senderWallet.setBalance(senderBalanceAfter);
        recipientWallet.setBalance(recipientBalanceAfter);

        walletRepository.save(senderWallet);
        walletRepository.save(recipientWallet);

        UUID referenceId = UUID.randomUUID();

        String description = normalizeDescription(
                request.getDescription()
        );

        CoinTransaction senderTransaction =
                createTransaction(
                    senderWallet,
                    TransactionType.TRANSFER_OUT,
                    amount,
                    senderBalanceAfter,
                    referenceId,
                    recipient.getId(),
                    description
                );

        CoinTransaction recipientTransaction =
                createTransaction(
                    recipientWallet,
                    TransactionType.TRANSFER_IN,
                    amount,
                    recipientBalanceAfter,
                    referenceId,
                    sender.getId(),
                    description
                );

        transactionRepository.save(senderTransaction);
        transactionRepository.save(recipientTransaction);
    }
    
    
    /**
     * 領取每日獎勵。
     */
    @Transactional
    public Long claimDailyReward(UUID userId) {

        CoinWallet wallet = getOrCreateWallet(userId);

        LocalDate today = LocalDate.now(TAIWAN_ZONE);

        ZonedDateTime startOfDay =
                today.atStartOfDay(TAIWAN_ZONE);

        ZonedDateTime startOfNextDay =
                today.plusDays(1).atStartOfDay(TAIWAN_ZONE);

        boolean alreadyClaimed =
                transactionRepository
                    .existsByWalletIdAndTransactionTypeAndCreateTimeGreaterThanEqualAndCreateTimeLessThan(
                        wallet.getId(),
                        TransactionType.DAILY_REWARD,
                        startOfDay.toInstant(),
                        startOfNextDay.toInstant()
                    );

        if (alreadyClaimed) {
            throw new IllegalArgumentException(
                "今天已經領取過每日獎勵"
            );
        }

        long balanceAfter =
                wallet.getBalance() + DAILY_REWARD_AMOUNT;

        wallet.setBalance(balanceAfter);

        walletRepository.save(wallet);

        CoinTransaction transaction =
                createTransaction(
                    wallet,
                    TransactionType.DAILY_REWARD,
                    DAILY_REWARD_AMOUNT,
                    balanceAfter,
                    UUID.randomUUID(),
                    null,
                    "每日登入獎勵"
                );

        transactionRepository.save(transaction);

        return DAILY_REWARD_AMOUNT;
    }
    
    //管理員加扣幣    
    @Transactional
    public Long adjustCoins(UUID adminUserId,CoinAdjustRequest request){
    	
    	if(!isSystemAdmin(adminUserId)) {
    		throw new SecurityException("只有系統管理員可以調整點數");
    	}
    	
    	if(request == null) {
    		 throw new IllegalArgumentException("調整資料不能為空");
    	}    	
        if(request.getAccount() == null || request.getAccount().isBlank()) {
        	 throw new IllegalArgumentException("請輸入會員帳號");        	 
         }
        if(request.getAmount() == null || request.getAmount()<= 0) {
        	 throw new IllegalArgumentException("調整金額必須大於0");
        }
    	if(request.getOperation() == null || request.getOperation().isBlank()) {
    	   	 throw new IllegalArgumentException("請選擇加幣或扣幣");
    	}
        if(request.getAccount()!= null && request.getDescription().length() >200) {
        	throw new IllegalArgumentException("調整原因不能超過200個字");
        }
        
        
        Users targetUser = usersRepository
                .findByAccount(request.getAccount().trim())
                .orElseThrow(() ->
                    new IllegalArgumentException("找不到指定會員帳號"));
        CoinWallet wallet =
                getOrCreateWallet(targetUser.getId());

        long amount = request.getAmount();
        long balanceAfter;

        TransactionType transactionType;

        String operation =
                request.getOperation().trim();
        
        if ("ADD".equalsIgnoreCase(operation)) {

            balanceAfter =
                    wallet.getBalance() + amount;

            transactionType =
                    TransactionType.ADMIN_ADD;

        } else if ("DEDUCT".equalsIgnoreCase(operation)) {

            if (wallet.getBalance() < amount) {
                throw new IllegalArgumentException(
                    "會員錢包餘額不足，無法扣幣"
                );
            }

            balanceAfter =
                    wallet.getBalance() - amount;

            transactionType =
                    TransactionType.ADMIN_DEDUCT;

        } else {
            throw new IllegalArgumentException(
                "operation 只能是 ADD 或 DEDUCT"
            );
        }
        wallet.setBalance(balanceAfter);
        walletRepository.save(wallet);
        
        String description =
                normalizeDescription(
                    request.getDescription()
                );

        if (description == null) {

            if (transactionType ==
                    TransactionType.ADMIN_ADD) {

                description = "管理員加幣";

            } else {
                description = "管理員扣幣";
            }
        }
        CoinTransaction transaction =
                createTransaction(
                    wallet,
                    transactionType,
                    amount,
                    balanceAfter,
                    UUID.randomUUID(),
                    adminUserId,
                    description
                );

        transactionRepository.save(transaction);

        return balanceAfter;
    }      
    
        
    
    //判斷會員是否為系統管理員
    private boolean isSystemAdmin(UUID userId) {
    	if(userId == null) {
    		return false;
    	}
    	
    	return userPermissionRepository
    			.existsByUserIdAndPermissionsNameIgnoreCase
    			(userId, SYSTEM_ADMIN_PERMISSION);
    }
    
        

    /**
     * 建立一筆交易紀錄。
     */
    private CoinTransaction createTransaction(
            CoinWallet wallet,
            TransactionType transactionType,
            Long amount,
            Long balanceAfter,
            UUID referenceId,
            UUID relatedUserId,
            String description) {

        CoinTransaction transaction = new CoinTransaction();

        transaction.setWallet(wallet);
        transaction.setTransactionType(transactionType);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setReferenceId(referenceId);
        transaction.setRelatedUserId(relatedUserId);
        transaction.setDescription(description);

        return transaction;
    }

    /**
     * 檢查轉帳資料。
     */
    private void validateTransferRequest(
            TransferRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("轉帳資料不能為空");
        }

        if (request.getRecipientAccount() == null ||
            request.getRecipientAccount().isBlank()) {

            throw new IllegalArgumentException("請輸入收款帳號");
        }

        if (request.getAmount() == null ||
            request.getAmount() <= 0) {

            throw new IllegalArgumentException(
                "轉帳金額必須大於 0"
            );
        }

        if (request.getDescription() != null &&
            request.getDescription().length() > 200) {

            throw new IllegalArgumentException(
                "轉帳備註不能超過 200 個字"
            );
        }
    }

    /**
     * 整理選填備註。
     */
    private String normalizeDescription(String description) {

        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }
}