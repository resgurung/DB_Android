package co.deshbidesh.db_android.db_settings_feature.viewmodel

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.deshbidesh.db_android.db_network.domain.DBSettingsRepository
import co.deshbidesh.db_android.db_settings_feature.models.DBSetting
import co.deshbidesh.db_android.shared.utility.DBPreferenceHelper
import co.deshbidesh.db_android.shared.utility.DBSettingConstants
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream

class DBSettingsViewModel(
        private val dbSettingsRepository: DBSettingsRepository,
        private val gson: Gson = Gson()
): ViewModel() {

//    var settingsData: MutableLiveData<Response<DBSettingsData>> = MutableLiveData()

//    var appSettings: MutableLiveData<Response<DBSetting>> = MutableLiveData()

//    fun getSettingsData(){
//        viewModelScope.launch {
//            settingsData.value =dbSettingsRepository.getSettingsData()
//        }
//    }

//    fun getSettingData(context: Context, settingData: (DBSetting) -> Unit) {
//
//        viewModelScope.launch(Dispatchers.IO) {
//
//            val jsonSettingData = DBPreferenceHelper.getStoredString(DBSettingConstants.SETTING_DATA, "")
//
//            if (!jsonSettingData.isNullOrEmpty()) {
//
//                val setting = gson.fromJson(jsonSettingData, DBSetting::class.java)
//
//                setting?.let {
//
//                    getSettingsDataFromNetwork(it.version.toInt())
//
//                    settingData(setting)
//                }
//
//            } else {
//
//                getSettingsDataFromNetwork(0)
//
//                getSettingsDataFromAsset(context.assets) {
//
//                    settingData(it)
//                }
//            }
//        }
//    }

    fun getSettings(context: Context): DBSetting? {

        val jsonSettingData = DBPreferenceHelper.getStoredString(DBSettingConstants.SETTING_DATA, "")

        if (!jsonSettingData.isNullOrEmpty()) {

            val setting = gson.fromJson(jsonSettingData, DBSetting::class.java)

            setting?.let {

                getSettingsFromNetwork(it.version.toInt())

                return it

            } ?: kotlin.run {

                return getSettingsDataFromResources(context.assets)
            }

        } else {

            getSettingsFromNetwork(0)

            return getSettingsDataFromResources(context.assets)
        }
    }

    private fun getSettingsFromNetwork(version: Int) {

        viewModelScope.launch {

            val response = dbSettingsRepository.getSettingsFromNetwork(version)

            if (response.code() != 304) {

                if (response.isSuccessful) {

                    response.body()?.let {

                        setSettingToPreferences(it)
                    }

                } else {

                    Log.d("Error", response.errorBody().toString() )
                }
            } else {
                // do nothing when 304
                Log.d("304", "data not changed")
            }
        }

    }

//
//    private suspend fun getSettingsDataFromNetwork(version: Int) {
//
//        dbSettingsRepository.getAppSettings(version) { response ->
//
//            if (response.code() != 304) {
//
//                if (response.isSuccessful) {
//
//                    response.body()?.let {
//
//                        setSettingToPreferences(it)
//                    }
//
//                } else {
//
//                    Log.d("Error", response.errorBody().toString() )
//                }
//            } else {
//                // do nothing when 304
//                Log.d("304", "data not changed")
//            }
//        }
//    }


    fun setSettingToPreferences(setting: DBSetting) {

        val jsonObj = gson.toJson(setting)

        DBPreferenceHelper.storeString(DBSettingConstants.SETTING_DATA, jsonObj)
    }

//    private fun getSettingsDataFromAsset(manager:AssetManager, listener: (DBSetting) -> Unit){
//
//        var inputStream: InputStream? = null
//
//        try {
//            inputStream = manager.open("dbsettings.json")
//
//            val jsonString = inputStream.bufferedReader().use { it.readText() }
//
//            val dbSettingsData = gson.fromJson(jsonString, DBSetting::class.java)
//
//            listener(dbSettingsData)
//
//        } catch (e: IOException){
//
//            Log.d("Asset", e.toString() )
//
//        } finally {
//
//            inputStream?.close()
//        }
//    }

    private fun getSettingsDataFromResources(manager:AssetManager): DBSetting? {

        var inputStream: InputStream? = null

        try {
            inputStream = manager.open("dbsettings.json")

            val jsonString = inputStream.bufferedReader().use { it.readText() }

            return gson.fromJson(jsonString, DBSetting::class.java)

        } catch (e: IOException){

            Log.d("Asset", e.toString() )

        } finally {

            inputStream?.close()
        }

        return null
    }
}

//{
//  "data": [
//    {
//      "ytURL": "https://www.youtube.com/channel/UC22Tp-7tetG-CwbnNPPkeWQ",
//      "contactPhNumber": "+447848188428",
//      "companyInfo": "Unity in diversity.",
//      "legalDisclaimer": "<h2>DISCLAIMER</h2>\n<h3>WEBSITE & MOBILE APP DISCLAIMER</h3>\n<p>\n    The information provided by Desh Bidesh (\"Desh Bidesh\", \"we\", \"us\" or \"our\") on <a href=\"/\"> Desh Bidesh </a> (the \"Site\") and our mobile application (the \"Desh Bidesh\") is for general informational purposes only. All information on the Site and our mobile application is provided in good faith, however we make no representation or warranty of any kind, express or implied, regarding the accuracy, adequacy, validity, reliability, availability or completeness of any information on the Site or our mobile application. UNDER NO CIRCUMSTANCE SHALL WE HAVE ANY LIABILITY TO YOU FOR ANY LOSS OR DAMAGE OF ANY KIND INCURRED AS A RESULT OF THE USE OF THE SITE OR OUR MOBILE APPLICATION OR RELIANCE ON ANY INFORMATION PROVIDED ON THE SITE AND OUR MOBILE APPLICATION. YOUR USE OF THE SITE AND OUR MOBILE APPLICATION AND YOUR RELIANCE ON ANY INFORMATION ON THE SITE AND OUR MOBILE APPLICATION IS SOLELY AT YOUR OWN RISK.\n</p>\n<p></p>\n<h3>EXTERNAL LINKS DISCLAIMER</h3>\n<p>\n    The Site and our mobile application may contain (or you may be sent through the Site or our mobile application) links to other websites or content belonging to or originating from third parties or links to websites and features in banners or other advertising. Such external links are not investigated, monitored, or checked for accuracy, adequacy, validity, reliability, availability or completeness by us. WE DO NOT WARRANT, ENDORSE, GUARANTEE, OR ASSUME RESPONSIBILITY FOR THE ACCURACY OR RELIABILITY OF ANY INFORMATION OFFERED BY THIRD-PARTY WEBSITES LINKED THROUGH THE SITE OR ANY WEBSITE OR FEATURE LINKED IN ANY BANNER OR OTHER ADVERTISING. WE WILL NOT BE A PARTY TO OR IN ANY WAY BE RESPONSIBLE FOR MONITORING ANY TRANSACTION BETWEEN YOU AND THIRD-PARTY PROVIDERS OF PRODUCTS OR SERVICES.\n</p>\n<p></p>\n<h6><b>Last updated March 10, 2020</b></h6>\n<p></p>",
//      "companyName": "Desh Bidesh",
//      "contactName": "Sumish Hirachan",
//      "privacy": "<h2><b>PRIVACY POLICY</b></h2>\n <p>\n Thank you for choosing to be part of our community at Desh Bidesh (\"Desh Bidesh\", \"we\", \"us\", or \"our\"). We are committed to protecting your personal information and your right to privacy. If you have any questions or concerns about our policy, or our practices with regards to your personal information, please contact us at \"informationdeshbidesh@gmail.com\" or fill a form on our website <a href=\"https://deshbidesh.co.uk\"> here</a>.\n </p>\n <p>\n When you visit our <a href=\"https://deshbidesh.co.uk\"> website</a>, mobile application \"Desh Bidesh\", and use our services, you trust us with your personal information. We take your privacy very seriously. In this privacy policy, we seek to explain to you in the clearest way possible what information we collect, how we use it and what rights you have in relation to it. We hope you take some time to read through it carefully, as it is important. If there are any terms in this privacy policy that you do not agree with, please discontinue use of our Sites or Apps and our services.\n </p>\n This privacy policy applies to all information collected through our website (such as <a href=\"https://deshbidesh.co.uk\"> Desh Bidesh </a>), mobile application, (\"Desh Bidesh\"), and/or any related services, sales, marketing or events (we refer to them collectively in this privacy policy as the \"Services\").\n <p/>\n <p>\n <b>Please read this privacy policy carefully as it will help you make informed decisions about sharing your personal information with us.</b>\n </p>\n \n <br>\n <div class=\"row\">\n     <div class=\"col-md-1\"></div>\n     <div class=\"col-md-11\">\n         <h4><b>TABLE OF CONTENTS</b></h4>\n         <li>\n             WHAT INFORMATION DO WE COLLECT?\n         </li>\n         <li>\n             HOW DO WE USE YOUR INFORMATION?\n         </li>\n         <li>\n             WILL YOUR INFORMATION BE SHARED WITH ANYONE?\n         </li>\n         <li>\n             WHO WILL YOUR INFORMATION BE SHARED WITH?\n         </li>\n         <li>\n             COOKIES\n         </li>\n         <li>\n             LINKS TO OTHER SITES\n         </li>\n         <li>\n             CHILDREN\n         </li>\n         <li>\n             SECURITY\n         </li>\n         <li>\n             CHANGES TO THIS PRIVACY POLICY\n         </li>\n         <li>\n             CONTACT INFORMATION\n         </li>\n     </div>\n </div>\n <br>\n<h4><b>WHAT INFORMATION DO WE COLLECT?</b></h4>\n<p>\n    We collect and process the following information which may include your personal data.\n</p>\n<p>\n    <b>a) Information provided by you when using the \"Desh Bidesh\" (Basic Information)</b>\n</p>\n<p>\n    We will collect the following information from you when you use our app:\n</p>\n<div class=\"row\">\n    <div class=\"col-md-1\"></div>\n    <div class=\"col-md-11\">\n        <li>\n            Your mobile device current region (i.e if you are in Great Britain \"GB\") so that we can show you contents from your country.\n        </li>\n        <br>\n    </div>\n</div>\n<p>\n    <b>b) Information used for the purposes of serving advertisements</b>\n</p>\n<p>\nThrough our third party advertising network partners <a href=\"https://support.google.com/admob/answer/6128543?hl=en\" target=\"_blank\">AdMob</a>, we may gather information about your devices when you install or use our app, depending on the permissions you have granted. For detail information please visit <a href=\"https://policies.google.com/technologies/partner-sites\" target=\"_blank\">Here</a>.\n</p>\n<p>\n    <b>c) To respond to your enquiries and requests for support</b>\n</p>\n<p>\n    We may process contact information (i.e. name & email) through our website\n    <a href=\"/\">Desh Bidesh</a> (we do not process email from \"Desh Bidesh\" mobile app)\n    which you have provided us from our \"contact us\" form or our email\n    \"informationdeshbidesh@gmail.com\" so that we are able to properly respond to your\n    enquiries and support requests.\n</p>\n<p>\n    <b>e) Information collected for the purposes of providing analytics</b>\n</p>\n<p>\n    We may collect technical information about your use of the Online Services through the use of tracking technologies and analytics with <a href=\"https://firebase.google.com/docs/analytics\" target=\"_blank\">Firebase Analytics</a>\n</p>\n<p>f) To help us improve the Online Services and fix any bugs</p>\n<p>\n  To provide better user experience we may collect information such as your device Internet\n  Protocol (\"IP\") address, device name, operating system\n  version, the configuration of the app when utilizing\n  our Service, the time and date of your use of the\n  Service, and other statistics via <a href=\"https://firebase.google.com/terms/crashlytics\" target=\"_blank\">Firebase Crashlytics</a>\n</p>\n\n<p>\n     <b>\n         We also may use the information in other ways for which we provide specific notice at the time of collection.\n     </b>\n     <ul>\n       <li>Push Notifications</li>\n         <p>\n         iOS : DeshBidesh uses Firebase Cloud Messaging (FCM) and Apple Push Notification Service (APNs) to inform users about new Article while the app is closed or in background. Upon creation of a new article, deshbidesh server passes a playload with json securly to FCM server. FCM uses Apple Push Notification Authentication Key to send Push Notifications to the application identified by the App ID. More info <a href=\"https://firebase.google.com/docs/cloud-messaging/fcm-architectures\" target=\"_blank\">FCM.</a>\n         </p>\n         <p>\n         The payload json contain article id, title, summary, image link, article link in the DeshBidesh server. The DeshBidesh app is started in the background for each incoming push notification, decrypts the push payload, and uses the article id to call DeshBidesh server for the complete data to show to the user (if enabled).The message you will receive will be based on your mobile device current region i.e.\"GB\" which means if your device is in \"GB\" you won't receive message for \"US\". We use FCM topics which is  publish/subscribe model. More info <a href=\"https://firebase.google.com/docs/cloud-messaging/ios/topic-messaging\" target=\"_blank\">FCM Topics</a>\n         </p>\n         <p>\n         User consent is always asked before we send any notifications. At any time, you can manage your push notification preferences or deactivate these notifications at any time by turning off the notification \"Settings\" in the App or in the Device Settings of your mobile device.\n         </p>\n       <li>Location Information</li>\n       <p>\n       iOS : When you use the App and with your consent, we use your precise (or GPS) location information to provide distance to the event. We don't collect this location information so this information. Please note: uesr can always turn it off from the device \"Settings\".\n       </p>\n       <li>Photos Roll</li>\n       iOS : To save photos from our DeshBidesh app, we will access your device camera roll with your permission. If you need to update your permissions, you can do so in the “Settings” app of your device.\n     </ul>\n</p>\n<br>\n <h4><b>HOW DO WE USE YOUR INFORMATION?</b></h4>\n <p>\n     In Short :  We process your information for purposes based on legitimate business interests, the fulfillment of our contract with you, compliance with our legal obligations, and/or your consent.\n </p>\n <p>\n  We use personal information collected via our Services or Apps for a variety of business purposes described below. We process your personal information for these purposes in reliance on our legitimate business interests, in order to enter into or perform a contract with you, with your consent, and/or for compliance with our legal obligations. We indicate the specific processing grounds we rely on next to each purpose listed below.\n </p>\n <p>\n     Below are some definitions that will help you understand the roles and responsibilities of Desh Bidesh:\n </p>\n     <div class=\"row\">\n         <div class=\"col-md-1\"></div>\n         <div class=\"col-md-11\">\n             <li>\n                 “data controller” means a person who (either alone or jointly or in common with other persons) determines the purposes for which and the manner in which any personal information are, or are to be used.\n             </li>\n             <li>\n                 “data processor”, in relation to personal information, means any person (other than an employee of the data controller) who processes the data on behalf of the data controller.\n             </li>\n             <br>\n         </div>\n     </div>\n\n     <p>If you provide the data and the instructions, then you are the data controller and Desh Bidesh is the data processor. If we determine the purposes for which we collect and use your personal information, then we are the Controller. We use the information we collect or receive:\n     </p>\n     <li>\n         <b>Deliver targeted advertising to you</b>. We may use your information to develop and display content and advertising (and work with third parties who do so) tailored to your interests and/or location and to measure its effectiveness.\n     </li>\n     <li>\n         <b>Request Feedback</b>. We may use your information to request feedback and to contact you about your use of our Services or Apps.\n     </li>\n     <li>\n         <b>To protect our Services</b>. We may use your information as part of our efforts to keep our Services or Apps safe and secure (for example, for fraud monitoring and prevention).\n\n     </li>\n     <li>\n         <b>To enforce our terms, conditions and policies for Business Purposes, Legal Reasons and Contractual.</b>\n     </li>\n     <li>\n         <b>To respond to legal requests and prevent harm</b>. If we receive a subpoena or other legal request, we may need to inspect the data we hold to determine how to respond.\n     </li>\n     <li>\n         <b>To deliver services to the user</b>. We may use your information to provide you with the requested service.\n     </li>\n     <li>\n         <b>To deliver better user experience with analytics</b>.\n     </li>\n \n \n <br>\n <h4><b>WILL YOUR INFORMATION BE SHARED WITH ANYONE?</b></h4>\n <p>\n     We only share information with your consent, to comply with laws, to provide you with services, to protect your rights, or to fulfill business obligations.\n </p>\n <p>\n     We may process or share data based on the following legal basis:\n </p>\n <li>\n     Consent: We may process your data if you have given us specific consent to use your personal information in a specific purpose.\n\n </li>\n <li>\n     Legitimate Interests: We may process your data when it is reasonably necessary to achieve our legitimate business interests.\n\n </li>\n <li>\n     Performance of a Contract: Where we have entered into a contract with you, we may process your personal information to fulfill the terms of our contract.\n </li>\n <li>\n     Legal Obligations: We may disclose your information where we are legally required to do so in order to comply with applicable law, governmental requests, a judicial proceeding, court order, or legal process, such as in response to a court order or a subpoena (including in response to public authorities to meet national security or law enforcement requirements).\n\n </li>\n <li>\n     Vital Interests: We may disclose your information where we believe it is necessary to investigate, prevent, or take action regarding potential violations of our policies, suspected fraud, situations involving potential threats to the safety of any person and illegal activities, or as evidence in litigation in which we are involved.\n </li>\n \n <br>\n <h4><b>WHO WILL YOUR INFORMATION BE SHARED WITH?</b></h4>\n <p>\n We only share and disclose your information with the following third parties. We have categorized each party so that you may be easily understand the purpose of our data collection and processing practices. If we have processed your data based on your consent and you wish to revoke your consent, please contact us at \"informationdeshbidesh@gmail.com\" or fill a form on our website <a href=\"/\"> here</a>.\n </p>\n <li> Embedded Videos from youtube - YouTube video embed. <a href=\"https://policies.google.com/privacy\"> Privacy Policy</a>\n </li>\n <br>\n <p> Moreover in the Mobile App we use Firebase (<a href=\"https://www.firebase.com/\">https://www.firebase.com/</a>), a framework maintained by the Google subsidiary Firebase residing in San Francisco, CA, USA, through which we track and administer the following real-time functions ––\n </p>\n <li>\n     Advertising through Firebase AdMob\n </li>\n <li>\n     Tracking of app crashes and their reasons through Firebase Crashlytics\n </li>\n <li>\n     Tracking of user behavior through Google Analytics for Firebase\n </li>\n <br>\n <p>For all mentioned Firebase services, only anonymized or pseudonymized user data is transmitted to Firebase (Google). Firebase’s privacy policy is available under <a href=\"https://www.firebase.com/terms/privacy-policy.html\"> Firebase Privacy Policy</a>.</p>\n \n \n <br>\n <h4><b>COOKIES</b></h4>\n <p>\n   Cookies are files with a small amount of data that are\n   commonly used as anonymous unique identifiers. These are\n   sent to your browser from the websites that you visit and\n   are stored on your device's internal memory.\n </p>\n <p>\n   This Service does not use these \"cookies\" explicitly.\n   However, the app may use third party code and libraries that\n   use \"cookies\" to collect information and improve their\n   services. You have the option to either accept or refuse\n   these cookies and know when a cookie is being sent to your\n   device. If you choose to refuse our cookies, you may not be\n   able to use some portions of this Service.\n </p>\n <p>\n Most web browsers allow you to control cookies through your browser settings. To find out more about cookies generally and how to manage or delete them, you can visit <a href=\"https://www.allaboutcookies.org/\" target=\"_blank\"> here</a>.\n </p>\n \n <br>\n <h4><b>LINKS TO OTHER SITES</b></h4>\n <p>\n This Service may contain links to other sites. If you click on a third-party link, you will be directed to that site. Note that these external sites are not operated by me. Therefore, I strongly advise you to review the Privacy Policy of these websites. I have no control over and assume no responsibility for the content, privacy policies, or practices of any third-party sites or services.\n </p>\n \n <br>\n <h4><b>CHILDREN</b></h4>\n <p>\n These Services do not address anyone under the age of 13. We do not knowingly collect personally identifiable information from children under 13. In the case we discover that a child under 13 has provided us with personal information, we will immediately delete this from our servers. If you are a parent or guardian and you are aware that your child has provided us with personal information, please contact us at \"informationdeshbidesh@gmail.com\" so that we will be able to take necessary actions.\n </p>\n \n <br>\n <h4><b>SECURITY</b></h4>\n <p>We value your trust in providing us your Personal Information, thus we are striving to use commercially acceptable means of protecting it. We will take all reasonable technical and organisational precautions to prevent the loss, misuse or alteration of your personal information. For example, our databases are password protected and limited to essential members only (members whose main role requires cms management).\n </p>\n <p>\n Please be aware that, although we endeavour to provide reasonable security for information we process and maintain, no security system can prevent all potential security breaches.\n </p>\n \n <br>\n <h4><b>CHANGES TO THIS PRIVACY POLICY</b></h4>\n <p>\n   We may update our Privacy Policy from\n   time to time. Thus, you are advised to review this page\n   periodically for any changes. We will\n   notify you of any changes by posting the new Privacy Policy\n   on this page. These changes are effective immediately after\n   they are posted on this page.\n </p>\n \n <br>\n <h4><b>CONTACT INFORMATION</b></h4>\n <p>\n   If you have any questions or suggestions about\n   our Privacy Policy, do not hesitate to contact\n   us at informationdeshbidesh@gmail.com or you can fill the form on the website <a href=\"/\"> Desh Bidesh </a>.\n </p>\n \n <br>\n <h6><b>Last updated March 10, 2020</b></h6>\n <p></p>",
//      "ttURL": "https://twitter.com/DeshBidesh4",
//      "position": "Enquiry Manager",
//      "companyId": "deshbidesh",
//      "contactEmail": "informationdeshbidesh@gmail.com",
//      "tncs": "<h2>Terms &amp; Conditions</h2>\n<p>Thank you for choosing to be part of our community at Desh Bidesh (\"Desh Bidesh\", \"we\", \"us\", or \"our\"). By downloading or using the app, these terms will\n  automatically apply to you – you should make sure therefore\n  that you read them carefully before using the app. you are\n  not allowed to copy, or modify the app, any part of the app,\n  or our trademarks in any way. You are not allowed to attempt\n  to extract the source code of the app, and you also\n  shouldn’t try to translate the app into other languages, or\n  make derivative versions. The app itself, and all the trade\n  marks, copyright, database rights and other intellectual\n  property rights related to it, still belong to\n  Desh Bidesh.\n</p>\n<p>\n  Desh Bidesh is committed to ensuring that the app\n  is as useful and efficient as possible. For that reason, we\n  reserve the right to make changes to the app or to charge\n  for its services, at any time and for any reason. We will\n  never charge you for the app or its services without making\n  it very clear to you exactly what you are paying for.\n</p>\n<p>\n  The \"Desh Bidesh app and website\" stores and processes personal data\n  that you have provided to us, in order to provide\n  our Service. It is your responsibility to keep your\n  phone and access to the app secure. We therefore recommend\n  that you do not jailbreak or root your mobile device, which is the\n  process of removing software restrictions and limitations\n  imposed by the official operating system of your device. It\n  could make your phone vulnerable to\n  malware/viruses/malicious programs, compromise your mobile device's security features and it could mean that the\n  \"Desh Bidesh\" app will not work properly or at all.\n</p>\n<p>\n  You should be aware that there are certain things that\n  Desh Bidesh will not take responsibility for.\n  Certain functions of the app will require the app to have an\n  active internet connection. The connection can be Wi-Fi, or\n  provided by your mobile network provider, but\n  Desh Bidesh cannot take responsibility for the\n  app not working at full functionality if you do not have\n  access to Wi-Fi, and you do not have any of your data\n  allowance left.\n</p>\n<p></p>\n<p>\n  If you are using the app outside of an area with Wi-Fi, you\n  should remember that your terms of the agreement with your\n  mobile network provider will still apply. As a result, you\n  may be charged by your mobile provider for the cost of data\n  for the duration of the connection while accessing the app,\n  or other third party charges. In using the app, you are\n  accepting responsibility for any such charges, including\n  roaming data charges if you use the app outside of your home\n  territory (i.e. region or country) without turning off data\n  roaming. If you are not the bill payer for the device on\n  which you are using the app, please be aware that we assume\n  that you have received permission from the bill payer for\n  using the app.\n</p>\n<p>\n  With respect to Desh Bidesh's responsibility for\n  your use of the app, when you are using the app, it is\n  important to bear in mind that although we endeavour to\n  ensure that it is updated and correct at all times, we do\n  rely on third parties to provide information to us so that\n  we can make it available to you.\n  Desh Bidesh accepts no liability for any loss,\n  direct or indirect, your experience as a result of relying\n  wholly on this functionality of the app.\n</p>\n<p>\n  At some point, we may wish to update the app. The app is\n  currently available on iOS operationg system (13 and higher) – the requirements for\n  system (and for any additional systems we\n  decide to extend the availability of the app to) may change,\n  and you’ll need to download the updates if you want to keep\n  using the app. Desh Bidesh does not promise that\n  it will always update the app so that it is relevant to you\n  and/or works with the  version that you have\n  installed on your device. However, you promise to always\n  accept updates to the application when offered to you, We\n  may also wish to stop providing the app, and may terminate\n  use of it at any time without giving notice of termination\n  to you. Unless we tell you otherwise, upon any termination,\n  (a) the rights and licenses granted to you in these terms\n  will end; (b) you must stop using the app, and (if needed)\n  delete it from your device.\n</p>\n<p><strong>Changes to This Terms and Conditions</strong></p> <p>\n  We may update our Terms and Conditions\n  from time to time. Thus, you are advised to review this page\n  periodically for any changes. I will\n  notify you of any changes by posting the new Terms and\n  Conditions on this page. These changes are effective\n  immediately after they are posted on this page.\n</p>\n<p><strong>Contact Us</strong></p>\n<p>\n  If you have any questions or suggestions about\n  our Terms and Conditions, do not hesitate to\n  contact us at \"informationdeshbidesh@gmail.com\" or you can fill the form on the website <a href=\"/\"> Desh Bidesh </a> .\n</p>\n<p></p>\n<h6><b>Last updated March 10, 2020</b></h6>\n<p></p>",
//      "igURL": "https://www.instagram.com/deshbideshapp",
//      "fbURL": "https://www.facebook.com/Desh-Bidesh-103355211335288",
//      "id": 1,
//      "companyLogo": "/Logos/deshbidesh.jpg"
//    }
//  ],
//  "type": "Setting"
//}
