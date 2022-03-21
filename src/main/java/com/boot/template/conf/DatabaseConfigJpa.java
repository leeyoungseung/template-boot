package com.boot.template.conf;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableJpaRepositories(
		basePackages = "com.boot.template.repo",
		entityManagerFactoryRef = "entityManagerFactory",
		transactionManagerRef = "transactionManager"
		)
public class DatabaseConfigJpa {
	
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
	@Primary
	public DataSource jpaDataSource() {
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
		
		log.info("JPA-Database-Source-Info : {}", this.toString());
		
		return new HikariDataSource(conf);
	}
	
	
	@Bean(name = "entityManagerFactory")
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(
			EntityManagerFactoryBuilder builder) {
		Map<String, String> prop = new HashMap<>();
		prop.put("hibernate.physical_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
		
		LocalContainerEntityManagerFactoryBean rep = 
				builder.dataSource(jpaDataSource())
				    .packages("com.boot.template.entity")
				    .properties(prop)
				    .build();
				 
		return rep;
		
	}
	
	
	@Primary
	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager(
	    EntityManagerFactoryBuilder builder) {
	  return new JpaTransactionManager(entityManagerFactory(builder).getObject());
	}


	@Override
	public String toString() {
		return "DatabaseConfigJpa [driverClassName=" + driverClassName + ", jdbcUrl=" + jdbcUrl + ", dbUserName="
				+ dbUserName + ", dbPassword=" + dbPassword + ", maximumPoolSize=" + maximumPoolSize + ", minimumIdle="
				+ minimumIdle + ", maxLifetime=" + maxLifetime + ", connectionTimeout=" + connectionTimeout
				+ ", idleTimeout=" + idleTimeout + "]";
	}
	
	
}
