package at.robbert.backend

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

private val loggers = ConcurrentHashMap<Class<*>, Logger>()
val <T : Any> T.log: Logger
    get() = loggers.getOrPut(this::class.java) {
        LoggerFactory.getLogger(this::class.java)
    }
