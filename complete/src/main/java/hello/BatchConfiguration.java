package hello;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public org.apache.tomcat.jdbc.pool.DataSource dataSource;

    // tag::readerwriterprocessor[]
    @Bean
    @StepScope//
    public FlatFileItemReader<Person> reader(@Value("#{jobParameters['input.file.name']}")String file) {
        FlatFileItemReader<Person> reader = new FlatFileItemReader<Person>();
        reader.setResource(new ClassPathResource(file));
        reader.setLineMapper(new DefaultLineMapper<Person>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "firstName", "lastName" });
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                setTargetType(Person.class);
            }});
        }});
        return reader;
    }

    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Person> writer() {
        JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<Person>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Person>());
        writer.setSql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)");
        writer.setDataSource(dataSource);
        return writer;
    }
    // end::readerwriterprocessor[]

    // tag::jobstep[]
    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener,ItemReader<Person> reader) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1(reader))
                .end()
                .build();
    }

    @Bean
    public Step step1(ItemReader<Person> reader) {
        return stepBuilderFactory.get("step1")
                .<Person, Person> chunk(10)//批处理每次处理10条数据
                .reader(reader)
                .processor(processor())
                .writer(writer())
                .build();
    }
    // end::jobstep[]
}
