package co.enoobong.rave.flutterwave.config

/**
 * @author Ibanga Enoobong I
 * @since 2/27/18.
 */
object ApiEndpoints {
    const val DIRECT_CHARGE = "flwv3-pug/getpaidx/api/charge"
    const val VALIDATE_CARD_CHARGE = "/flwv3-pug/getpaidx/api/validatecharge"
    const val VALIDATE_ACCOUNT_CHARGE = "/flwv3-pug/getpaidx/api/validate"
    const val REQUERY_TRANSACTION = "/flwv3-pug/getpaidx/api/verify"
    const val GET_BANKS = "/flwv3-pug/getpaidx/api/flwpbf-banks.js?json=1"
    const val CHARGE_TOKEN = "/flwv3-pug/getpaidx/api/tokenized/charge"
    const val CHECK_FEE = "/flwv3-pug/getpaidx/api/fee"
}