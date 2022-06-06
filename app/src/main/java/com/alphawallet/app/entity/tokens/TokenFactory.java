package com.alphawallet.app.entity.tokens;

import android.text.TextUtils;

import com.alphawallet.app.entity.ContractType;
import com.alphawallet.app.entity.NetworkInfo;
import com.alphawallet.app.repository.EthereumNetworkRepositoryType;
import com.alphawallet.app.repository.entity.RealmStaticToken;
import com.alphawallet.app.repository.entity.RealmToken;
import com.alphawallet.app.repository.entity.RealmWalletToken;
import com.alphawallet.app.util.Utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by James on 27/01/2018.
 *
 * TODO: Convert to builder model
 */

public class TokenFactory
{
    public Token createToken(TokenInfo tokenInfo, BigDecimal balance, List<BigInteger> balances, long updateBlancaTime, ContractType type, String networkName, long lastBlockCheck)
    {
        Token thisToken;
        switch (type)
        {
            case ERC875:
            case ERC875_LEGACY:
                if (balances == null) balances = new ArrayList<>();
                thisToken = new Ticket(tokenInfo, balances, updateBlancaTime, networkName, type);
                break;
            case ERC721_TICKET:
                if (balances == null) balances = new ArrayList<>();
                thisToken = new ERC721Ticket(tokenInfo, balances, updateBlancaTime, networkName, type);
                break;
            case ERC721:
            case ERC721_LEGACY:
                if (tokenInfo.decimals != 0)
                {
                    tokenInfo = new TokenInfo(tokenInfo.address, tokenInfo.name, tokenInfo.symbol, 0, tokenInfo.isEnabled, tokenInfo.chainId);
                }
                thisToken = new ERC721Token(tokenInfo, null, balance, updateBlancaTime, networkName, type);
                if (balance.compareTo(BigDecimal.ZERO) >=0)
                {
                    thisToken.balance = balance;
                }
                break;
            case ERC1155:
                tokenInfo = new TokenInfo(tokenInfo.address, tokenInfo.name, tokenInfo.symbol, 0, tokenInfo.isEnabled, tokenInfo.chainId);
                thisToken = new ERC1155Token(tokenInfo, null, updateBlancaTime, networkName);
                thisToken.balance = balance;
                break;
            case NOT_SET:
            case ERC20:
            case MAYBE_ERC20:
            case OTHER:
            case CURRENCY:
            case DELETED_ACCOUNT:
            case DYNAMIC_CONTRACT:
            case LEGACY_DYNAMIC_CONTRACT:
            case CREATION:
            case ETHEREUM:
            case ETHEREUM_INVISIBLE:
                thisToken = new Token(tokenInfo, balance, updateBlancaTime, networkName, type);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        thisToken.lastBlockCheck = lastBlockCheck;

        return thisToken;
    }

    public Token createToken(TokenInfo tokenInfo, RealmToken realmItem, long updateBlancaTime, String networkName)
    {
        Token thisToken;
        int typeOrdinal = realmItem.getInterfaceSpec();
        if (typeOrdinal > ContractType.CREATION.ordinal()) typeOrdinal = ContractType.NOT_SET.ordinal();

        ContractType type = ContractType.values()[typeOrdinal];
        String realmBalance = realmItem.getBalance();
        BigDecimal decimalBalance;
        if (TextUtils.isEmpty(realmBalance)) realmBalance = "0";
        if (Utils.isNumeric(realmBalance))
        {
            decimalBalance = new BigDecimal(realmBalance);
        }
        else
        {
            decimalBalance = BigDecimal.ZERO;
        }

        switch (type)
        {
            case ETHEREUM_INVISIBLE:
                tokenInfo.isEnabled = false;
                thisToken = new Token(tokenInfo, decimalBalance, updateBlancaTime, networkName, type);
                thisToken.pendingBalance = decimalBalance;
                break;
            case ETHEREUM:
                tokenInfo.isEnabled = true; //native eth always enabled
            case ERC20:
            case DYNAMIC_CONTRACT:
                thisToken = new Token(tokenInfo, decimalBalance, updateBlancaTime, networkName, type);
                thisToken.pendingBalance = decimalBalance;
                break;
            case ERC721_TICKET:
                if (realmBalance.equals("0")) realmBalance = "";
                thisToken = new ERC721Ticket(tokenInfo, realmBalance, updateBlancaTime, networkName, type);
                break;
            case ERC875:
            case ERC875_LEGACY:
                if (realmBalance.equals("0")) realmBalance = "";
                thisToken = new Ticket(tokenInfo, realmBalance, updateBlancaTime, networkName, type);
                break;

            case CURRENCY:
                thisToken = new Token(tokenInfo, decimalBalance, updateBlancaTime, networkName, ContractType.ETHEREUM);
                thisToken.pendingBalance = decimalBalance;
                break;

            case ERC721:
            case ERC721_LEGACY:
                thisToken = new ERC721Token(tokenInfo, null, decimalBalance, updateBlancaTime, networkName, type);
                break;

            case ERC1155:
                thisToken = new ERC1155Token(tokenInfo, null, updateBlancaTime, networkName);
                break;

            case OTHER:
            default:
                thisToken = new Token(tokenInfo, BigDecimal.ZERO, updateBlancaTime, networkName, type);
                break;

        }

        thisToken.setupRealmToken(realmItem);

        return thisToken;
    }

    // new
    public Token createToken(TokenInfo tokenInfo, RealmWalletToken walletToken, RealmStaticToken staticToken, long updateBlancaTime, String networkName)
    {
        Token thisToken;
        int typeOrdinal = (staticToken != null) ? staticToken.getInterfaceSpec() : ContractType.ETHEREUM.ordinal(); // if eth-xxx
        if (typeOrdinal > ContractType.CREATION.ordinal()) typeOrdinal = ContractType.NOT_SET.ordinal();

        ContractType type = ContractType.values()[typeOrdinal];
        String realmBalance = walletToken.getBalance();
        BigDecimal decimalBalance;
        if (TextUtils.isEmpty(realmBalance)) realmBalance = "0";
        if (Utils.isNumeric(realmBalance))
        {
            decimalBalance = new BigDecimal(realmBalance);
        }
        else
        {
            decimalBalance = BigDecimal.ZERO;
        }

        switch (type)
        {
            case ETHEREUM_INVISIBLE:
                tokenInfo.isEnabled = false;
                thisToken = new Token(tokenInfo, decimalBalance, updateBlancaTime, networkName, type);
                thisToken.pendingBalance = decimalBalance;
                break;
            case ETHEREUM:
                tokenInfo.isEnabled = true; //native eth always enabled
            case ERC20:
            case DYNAMIC_CONTRACT:
                thisToken = new Token(tokenInfo, decimalBalance, updateBlancaTime, networkName, type);
                thisToken.pendingBalance = decimalBalance;
                break;
            case ERC721_TICKET:
                if (realmBalance.equals("0")) realmBalance = "";
                thisToken = new ERC721Ticket(tokenInfo, realmBalance, updateBlancaTime, networkName, type);
                break;
            case ERC875:
            case ERC875_LEGACY:
                if (realmBalance.equals("0")) realmBalance = "";
                thisToken = new Ticket(tokenInfo, realmBalance, updateBlancaTime, networkName, type);
                break;

            case CURRENCY:
                thisToken = new Token(tokenInfo, decimalBalance, updateBlancaTime, networkName, ContractType.ETHEREUM);
                thisToken.pendingBalance = decimalBalance;
                break;

            case ERC721:
            case ERC721_LEGACY:
                thisToken = new ERC721Token(tokenInfo, null, decimalBalance, updateBlancaTime, networkName, type);
                break;

            case ERC1155:
                thisToken = new ERC1155Token(tokenInfo, null, updateBlancaTime, networkName);
                break;

            case OTHER:
            default:
                thisToken = new Token(tokenInfo, BigDecimal.ZERO, updateBlancaTime, networkName, type);
                break;

        }

        thisToken.setupRealmToken(walletToken);

        return thisToken;
    }

    public Token createToken(TokenInfo tokenInfo, ContractType type, String networkName)
    {
        Token thisToken;
        long currentTime = System.currentTimeMillis();
        switch (type)
        {
            case ERC875:
            case ERC875_LEGACY:
                thisToken = new Ticket(tokenInfo, new ArrayList<BigInteger>(), currentTime, networkName, type);
                break;
            case ERC721_TICKET:
                thisToken = new ERC721Ticket(tokenInfo, new ArrayList<BigInteger>(), currentTime, networkName, type);
                break;
            case ERC721:
            case ERC721_LEGACY:
            case ERC721_UNDETERMINED:
                thisToken = new ERC721Token(tokenInfo, null, BigDecimal.ZERO, currentTime, networkName, type);
                break;
            case ETHEREUM:
                String[] split = tokenInfo.address.split("-");
                thisToken = new Token(
                        new TokenInfo(split[0],
                                      tokenInfo.name,
                                      tokenInfo.symbol,
                                      tokenInfo.decimals,
                                      true,
                                      tokenInfo.chainId),
                        BigDecimal.ZERO, currentTime, networkName, type);
                thisToken.pendingBalance = BigDecimal.ZERO;
                break;
            case ERC1155:
                thisToken = new ERC1155Token(tokenInfo, null, currentTime, networkName);
                break;
            case ERC20:
            case DYNAMIC_CONTRACT:
            default:
                thisToken = new Token(
                        new TokenInfo(tokenInfo.address,
                                      tokenInfo.name,
                                      tokenInfo.symbol,
                                      tokenInfo.decimals,
                                      true,
                                      tokenInfo.chainId),
                        BigDecimal.ZERO, currentTime, networkName, type);
                break;
        }

        return thisToken;
    }

    public TokenInfo createTokenInfo(RealmToken realmItem, RealmStaticToken realmStaticToken)
    {
        if (realmItem.getTokenAddress().equals("eth"))
        {
            return new TokenInfo();
        }
        return new TokenInfo(realmStaticToken.getTokenAddress(), realmStaticToken.getName(), realmStaticToken.getSymbol(), realmStaticToken.getDecimals(),
                realmItem.getEnabled(), realmStaticToken.getChainId());
    }

    public TokenInfo createTokenInfo(RealmWalletToken walletToken, RealmStaticToken realmStaticToken)
    {
        if (walletToken.getTokenAddress().equals("eth"))
        {
            return new TokenInfo();
        }
        return new TokenInfo(realmStaticToken.getTokenAddress(), realmStaticToken.getName(), realmStaticToken.getSymbol(), realmStaticToken.getDecimals(),
                walletToken.isEnabled(), realmStaticToken.getChainId());
    }

    public TokenInfo createTokenInfo(EthereumNetworkRepositoryType networkRepository, RealmWalletToken walletToken, RealmStaticToken staticToken, String walletAddress)
    {
        Timber.d("createTokenInfo: walletToken: %s, staticToken: %s, walletAddress: %s", walletToken, staticToken, walletAddress);
        try
        {
            if (walletToken.getTokenAddress().equals("eth"))
            {
                NetworkInfo ni = networkRepository.getNetworkByChain(walletToken.getChainId());
                TokenInfo info = new TokenInfo(walletAddress, ni.name, ni.symbol, 18, walletToken.isEnabled(), walletToken.getChainId());
                info.isEnabled = walletToken.isEnabled();
                return info;
            }
            return new TokenInfo(staticToken.getTokenAddress(), staticToken.getName(), staticToken.getSymbol(), staticToken.getDecimals(),
                    walletToken.isEnabled(), staticToken.getChainId());
        }
        catch (Exception e)
        {
            Timber.d("createTokenInfo: cannot create Token: ");
            return null;
        }
    }
}
