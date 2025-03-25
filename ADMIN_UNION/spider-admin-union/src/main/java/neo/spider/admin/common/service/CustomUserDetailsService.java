//package neo.spider.admin.common.service;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import neo.spider.admin.common.dto.FwkUserDTO;
//import neo.spider.admin.common.mapper.AuthMapper;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    @Autowired
//    private AuthMapper userMapper;
//    
////    @Override
////    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
////        // FwkUser 테이블에서 사용자 조회
////        FwkUser user = userRepository.findByUserId(username)
////                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
////        
////        // 권한 설정 (예: ROLE_USER 등)
////        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));
////        return new User(
////                user.getUserId(),
////                user.getPassword(),
////                authorities
////        );
////    }
//    
//	    @Override
//	    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//	        // 데이터베이스나 다른 곳에서 사용자 정보 로드
//	        // 예시로 하드코딩된 사용자 정보 사용
//	    	
//	    	List<FwkUserDTO> users = userMapper.findById(username);
//	    	
//	        if (users == null) {
//	            throw new UsernameNotFoundException("User not found with username: " + username);
//	        }
//	        
//	        FwkUserDTO user = users.get(0);
//            return User.builder()
//                    .username(user.getUserId())
//                    .password(user.getPassword()) // "password"의 BCrypt 암호화된 값
//                    .roles("ADMIN")
//                    .build();
//	    }
//    
//}