package co.enoobong.rave.flutterwave.data

/**
 * @author Ibanga Enoobong I
 * @since 3/2/18.
 */
class Callbacks {

    interface OnChargeRequestComplete {
        fun onSuccess(response: ApiResponse<ChargeResponseData>, responseAsJSONString: String)
        fun onError(message: String?, responseAsJSONString: String?)
    }

    interface OnValidateChargeCardRequestComplete {
        //        fun onSuccess(response: ChargeResponse, responseAsJSONString: String)
        fun onError(message: String?, responseAsJSONString: String)
    }

    interface OnRequeryRequestComplete {
        //        fun onSuccess(response: RequeryResponse, responseAsJSONString: String)
        fun onError(message: String, responseAsJSONString: String)
    }

    interface OnGetBanksRequestComplete {
        fun onSuccess(banks: List<Bank>)
        fun onError(message: String?)
    }

    interface BankSelectedListener {
        fun onBankSelected(b: Bank)
    }

    interface SavedCardSelectedListener {
        fun onCardSelected(savedCard: SavedCard)
    }

    interface OnGetFeeRequestComplete {
        //        fun onSuccess(response: FeeCheckResponse)
        fun onError(message: String)
    }
}
