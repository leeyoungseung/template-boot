package com.boot.template.conf;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableTransactionManagement
@MapperScan("com.boot.template.mapper")
public class DatabaseConfigMybatis {

	@Value("${spring.datasource.driver-class-name}")
	private String driverClassName;
	
	@Value("${spring.datasource.url}")
	private String jdbcUrl;
	
	@Value("${spring.datasource.username}")
	private String dbUserName;
	
	@Value("${spring.datasource.password}")
	private String dbPassword;
	
	@Value("${spring.datasource.hikari.maximum-pool-size}")
	private Integer maximumPoolSize;
	
	@Value("${spring.datasource.hikari.minimum-idle}")
	private Integer minimumIdle;
	
	@Value("${spring.datasource.hikari.max-lifetime}")
	private Integer maxLifetime;
	
	@Value("${spring.datasource.hikari.connection-timeout}")
	private Integer connectionTimeout;
	
	@Value("${spring.datasource.hikari.idle-timeout}")
	private Integer idleTimeout;
	
	
	@Bean
	public DataSource mybatisDataSource() {
		HikariConfig conf = new HikariConfig();		
		conf.setDriverClassName(driverClassName);
		conf.setJdbcUrl(jdbcUrl);
		conf.setUsername(dbUserName);
		conf.setPassword(dbPassword);
		conf.setMaximumPoolSize(maximumPoolSize);
		conf.setMinimumIdle(minimumIdle);
		conf.setMaxLifetime(maxLifetime);
		conf.setConnectionTimeout(connectionTimeout);
		conf.setIdleTimeout(idleTimeout);
		
		log.info("Mybatis-Database-Source-Info : {}", this.toString());
	    
	    return new HikariDataSource(conf);
	}
	
	@Bean(name= "batisSqlSessionFactory")
	public SqlSessionFactory batisSqlSessionFactory(
			@Qualifier("mybatisDataSource") DataSource mybatisDataSource,
			ApplicationContext applicationContext) 
					throws Exception {
		SqlSessionFactoryBean sqlSession = new SqlSessionFactoryBean();
		sqlSession.setDataSource(mybatisDataSource);
		sqlSession.setConfigLocation(applicationContext.getResource("classpath:mybatis-config.xml"));
		sqlSession.setMapperLocations(applicationContext.getResources("classpath:/mapper/**/*SQL.xml"));
        
		return sqlSession.getObject();
		
	}
	
    @Bean(name = "batisSqlSessionTemplate")
    public SqlSessionTemplate batisSqlSessionTemplate(SqlSessionFactory batisSqlSessionFactory) throws Exception {
 
        return new SqlSessionTemplate(batisSqlSessionFactory);
    }

	@Override
	public String toString() {
		return "DatabaseConfigMybatis [driverClassName=" + driverClassName + ", jdbcUrl=" + jdbcUrl + ", dbUserName="
				+ dbUserName + ", dbPassword=" + dbPassword + ", maximumPoolSize=" + maximumPoolSize + ", minimumIdle="
				+ minimumIdle + ", maxLifetime=" + maxLifetime + ", connectionTimeout=" + connectionTimeout
				+ ", idleTimeout=" + idleTimeout + "]";
	}
    
    
}
