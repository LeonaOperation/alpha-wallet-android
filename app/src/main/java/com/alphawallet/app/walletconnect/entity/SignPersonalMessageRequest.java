package com.alphawallet.app.walletconnect.entity;

import com.alphawallet.token.entity.EthereumMessage;
import com.alphawallet.token.entity.SignMessageType;
import com.alphawallet.token.entity.Signable;

public class SignPersonalMessageRequest extends BaseRequest
{
    public SignPersonalMessageRequest(String params)
    {
        super(params, WCEthereumSignMessage.WCSignType.PERSONAL_MESSAGE);
    }

    public Signable getSignable() {
        return new EthereumMessage(getMessage(), "", 0, SignMessageType.SIGN_PERSONAL_MESSAGE);
    }
}
