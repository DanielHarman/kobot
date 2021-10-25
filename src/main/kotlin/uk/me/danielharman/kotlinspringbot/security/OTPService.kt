package uk.me.danielharman.kotlinspringbot.security

import com.bastiaanjansen.otp.OTPGenerator
import com.bastiaanjansen.otp.SecretGenerator
import com.bastiaanjansen.otp.TOTPGenerator
import com.google.common.cache.CacheBuilder
import com.google.common.cache.LoadingCache
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.TimeUnit


@Service
class OTPService(private val otpGenerator: TOTPGenerator) {

    private val otpCache = CacheBuilder.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build<String,String>()

    fun getOtp(userId: String): String {
        val otp = otpGenerator.generate()
        otpCache.put(userId, otp);
        return otp
    }

    fun verifyOtp(userId: String, otp: String): Boolean{
        return otpGenerator.verify(otp) && otpCache.getIfPresent(userId) == otp
    }

}


@Configuration
class OtpConfig {

    @Bean
    fun provideTOTPGenerator(): TOTPGenerator {
        val secret = SecretGenerator.generate()
        return TOTPGenerator.Builder(secret)
            .withPasswordLength(8)
            .withPeriod(Duration.ofMinutes(5))
            .build()
    }
}