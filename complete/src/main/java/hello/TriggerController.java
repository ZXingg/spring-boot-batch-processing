package hello;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ZXing at 2018/4/14
 * QQ:1490570560
 */
@RestController
public class TriggerController {

    @Autowired
    Job importUserJob;
    @Autowired
    JobLauncher jobLauncher;

    public JobParameters jobParameters;

    @GetMapping("/read")
    public String read(String file) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobParameters=new JobParametersBuilder()
                .addString("input.file.name",file)
                .toJobParameters();

        jobLauncher.run(importUserJob,jobParameters);

        return "ok!";
    }
}
