rootProject.name = "ClientShieldAPI"

include("examples:MiscExamples")
include("examples:ClientControl")
include("examples:ClientSysinfo")
include("examples:AuthMeCookies")

file("local.settings.gradle.kts").let { if(it.exists()) apply(from = it) }
