package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class Application {

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
        return new ServletRegistrationBean(actionServlet, "/moviefun/*");
    }


    @Qualifier("albums-datasource")
    @Bean("albums-datasource")
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(serviceCredentials.jdbcUrl("albums-mysql", "p-mysql"));
        return dataSource;
    }

    @Qualifier("movies-datasource")
    @Bean("movies-datasource")
    public DataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(serviceCredentials.jdbcUrl("movies-mysql", "p-mysql"));
        return dataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter createHibernateJpaVendorAdapter()
    {
        HibernateJpaVendorAdapter rtn = new HibernateJpaVendorAdapter();
        rtn.setDatabase(Database.MYSQL);
        rtn.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        rtn.setGenerateDdl(true);
        return rtn;
    }

    @Qualifier("albums-entity-manager")
    @Bean
    public LocalContainerEntityManagerFactoryBean  createAlbumsEntityManagerFactory( @Qualifier("albums-datasource") DataSource dataSource, HibernateJpaVendorAdapter vendorAdapter)
    {
        LocalContainerEntityManagerFactoryBean rtn = new LocalContainerEntityManagerFactoryBean();
        rtn.setDataSource(dataSource);
        rtn.setJpaVendorAdapter(vendorAdapter);
        rtn.setPackagesToScan("org.superbiz.moviefun.albums");
        rtn.setPersistenceUnitName("albums");
        return rtn;
    }

    @Qualifier("movies-entity-manager")
    @Bean
    public LocalContainerEntityManagerFactoryBean  createMoviesEntityManagerFactory( @Qualifier("movies-datasource") DataSource dataSource, HibernateJpaVendorAdapter vendorAdapter)
    {
        LocalContainerEntityManagerFactoryBean rtn = new LocalContainerEntityManagerFactoryBean();
        rtn.setDataSource(dataSource);
        rtn.setJpaVendorAdapter(vendorAdapter);
        rtn.setPackagesToScan("org.superbiz.moviefun.movies");
        rtn.setPersistenceUnitName("movies");
        return rtn;
    }


    @Qualifier("albums-transaction-manager")
    @Bean
    public PlatformTransactionManager createAlbumPlatformTransactionManager(
            @Qualifier("albums-entity-manager") EntityManagerFactory factory )
    {
        return new JpaTransactionManager(factory);
    }


    @Qualifier("movies-transaction-manager")
    @Bean
    public PlatformTransactionManager createMoviesPlatformTransactionManager(
            @Qualifier("movies-entity-manager") EntityManagerFactory factory )
    {
        return new JpaTransactionManager(factory);
    }


    @Qualifier("albums-transaction-operations")
    @Bean
    public TransactionOperations createAlbumTransactionOperations(@Qualifier("albums-transaction-manager") PlatformTransactionManager ptm )
    {
        return new TransactionTemplate(ptm);
    }

    @Qualifier("movies-transaction-operations")
    @Bean
    public TransactionOperations createMovieTransactionOperations(@Qualifier("movies-transaction-manager") PlatformTransactionManager ptm )
    {
        return new TransactionTemplate(ptm);
    }



}
