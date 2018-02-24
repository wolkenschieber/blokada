package org.blokada.main

import android.content.Context
import com.github.salomonbrys.kodein.*
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import gs.environment.Journal
import org.blokada.property.IHostlineProcessor
import org.blokada.property.hostnameRegex

fun newFirebaseModule(ctx: Context): Kodein.Module {
    return Kodein.Module {
        bind<FirebaseAnalytics>() with singleton {
            FirebaseAnalytics.getInstance(instance())
        }
        bind<Journal>(overrides = true) with singleton {
            AFirebaseJournal(firebase = provider())
        }
        bind<IHostlineProcessor>(overrides = true) with singleton {
            object : IHostlineProcessor {
                override fun process(line: String): String? {
                    var l = line
                    if (l.startsWith("#")) return null
                    if (l.startsWith("<")) return null
                    if (l.endsWith("firebase.com")) return null
                    l = l.replaceFirst("0.0.0.0 ", "")
                    l = l.replaceFirst("127.0.0.1 ", "")
                    l = l.replaceFirst("127.0.0.1	", "")
                    l = l.trim()
                    if (l.isEmpty()) return null
                    if (!hostnameRegex.containsMatchIn(l)) return null
                    return l
                }
            }
        }
        onReady {
            FirebaseApp.initializeApp(ctx)
        }
    }
}

