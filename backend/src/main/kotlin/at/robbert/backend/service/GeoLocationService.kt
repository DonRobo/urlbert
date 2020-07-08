package at.robbert.backend.service

import com.maxmind.geoip2.DatabaseReader
import org.springframework.stereotype.Service
import java.io.File
import java.net.InetAddress

@Service
class GeoLocationService {
    private final val dbReader: DatabaseReader

    init {
        val database = File("GeoLite2-Country.mmdb")
        dbReader = DatabaseReader.Builder(database).build()
    }

    fun locate(ipAddress: InetAddress): String {
        return dbReader.tryCountry(ipAddress).orElse(null)?.country?.isoCode ?: "Country not found"
    }
}
