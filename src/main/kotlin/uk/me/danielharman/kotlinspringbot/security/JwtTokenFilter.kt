package uk.me.danielharman.kotlinspringbot.security

import io.ktor.http.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtTokenFilter(private val jwtTokenUtil: JwtTokenUtil, private val dashboardUserService: DashboardUserService): OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val header = request.getHeader(HttpHeaders.Authorization)

        // Get AUTH header and validate
        if(header.isNullOrEmpty() || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        // Get jwt token and validate
        val token = header.split(" ")[1].trim()
        if (!jwtTokenUtil.validate(token)){
            filterChain.doFilter(request,response)
            return
        }

        // Get user identity and set it on the security context
        val userDetails = dashboardUserService.loadUserByUsername(jwtTokenUtil.getUserId(token))
        if (userDetails == null) {
            filterChain.doFilter(request,response)
            return
        }

        val authentication = UsernamePasswordAuthenticationToken(userDetails,null, userDetails.authorities)
        authentication.details = WebAuthenticationDetailsSource().buildDetails(request);
        SecurityContextHolder.getContext().authentication = authentication
        filterChain.doFilter(request,response)
    }
}