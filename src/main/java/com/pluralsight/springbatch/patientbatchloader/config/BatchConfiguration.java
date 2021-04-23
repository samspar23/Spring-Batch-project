package com.pluralsight.springbatch.patientbatchloader.config;

import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Component
@EnableBatchProcessing
public class BatchConfiguration implements BatchConfigurer {
    private JobRepository jobRepository;
    private JobExplorer jobExplorer;
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier(value = "batchTransactionManager")
    private PlatformTransactionManager batchTransactionManager;

    @Autowired
    @Qualifier(value = "batchDataSource")
    private DataSource batchDataSource;

    @Override
    public JobRepository getJobRepository() throws Exception {
        return this.jobRepository;
    }

    @Override
    public PlatformTransactionManager getTransactionManager() throws Exception {
        return this.batchTransactionManager;
    }

    @Override
    public JobLauncher getJobLauncher() throws Exception {
        return this.jobLauncher;
    }

    @Override
    public JobExplorer getJobExplorer() throws Exception {
        return this.jobExplorer;
    }

    protected JobLauncher createJobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(this.jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    protected JobRepository createJobRepository() throws Exception {
        JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
        jobRepositoryFactoryBean.setDataSource(this.batchDataSource);
        jobRepositoryFactoryBean.setTransactionManager(this.batchTransactionManager);
        jobRepositoryFactoryBean.afterPropertiesSet();
        return jobRepositoryFactoryBean.getObject();
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        this.jobRepository = createJobRepository();
        JobExplorerFactoryBean jobExplorerFactoryBean = new JobExplorerFactoryBean();
        jobExplorerFactoryBean.setDataSource(this.batchDataSource);
        jobExplorerFactoryBean.afterPropertiesSet();
        this.jobExplorer = jobExplorerFactoryBean.getObject();
        this.jobLauncher = createJobLauncher();
    }
}
