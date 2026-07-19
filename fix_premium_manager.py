with open("app/src/main/java/com/loanmaster/pro/core/managers/PremiumManager.kt", "r") as f:
    content = f.read()

content = content.replace(
    'val persisted = context.dataStore.data.first()[IS_PREMIUM_UNLOCKED] ?: false\n            _isPremium.value = persisted',
    'context.dataStore.data.collect { preferences ->\n                val persisted = preferences[IS_PREMIUM_UNLOCKED] ?: false\n                _isPremium.value = persisted\n            }'
)

with open("app/src/main/java/com/loanmaster/pro/core/managers/PremiumManager.kt", "w") as f:
    f.write(content)
