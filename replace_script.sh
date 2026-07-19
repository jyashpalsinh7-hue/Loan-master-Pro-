#!/bin/bash
sed -i 's/showPremiumDialog = true/requestPremiumUnlock()/g' app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt
