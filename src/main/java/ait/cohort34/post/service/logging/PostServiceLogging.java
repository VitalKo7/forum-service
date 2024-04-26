package ait.cohort34.post.service.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Service;

@Service
@Aspect
@Slf4j(topic = "Post service")
public class PostServiceLogging {

    @Pointcut("execution(* ait.cohort34.post.service.PostServiceImpl.findPostById(String)) && args(id)")
    public void findById(String id){}

    @Pointcut("execution(public Iterable<ait.cohort34.post.dto.PostDto> ait.cohort34.post.service.PostServiceImpl.findPosts*(..))")
    public void bulkFind(){}

    @Pointcut("@annotation(PostLogger) && args(id, ..)")
    public void annotatedPostLogger(String id){}

    @Before("findById(id)")
    public void getPostLogging(String id){
        log.info("post with id: {}, requested ", id);
    }

    @Around("bulkFind()")
    public Object bulkFindLogging(ProceedingJoinPoint pjp) throws Throwable {

        Object[] args = pjp.getArgs();
        long start = System.currentTimeMillis();

        Object result = pjp.proceed(args);
        long end = System.currentTimeMillis();

        log.info("method: {} took: {} ms", pjp.getSignature().getName(), end - start);

        return result;
    }

    @AfterReturning("annotatedPostLogger(id)")
    public void changePostLogging(String id){
        log.info("change post with id: {}, requested ", id);
    }

}
