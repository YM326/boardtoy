package com.example.demo.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.appstream.AmazonAppStream;
import com.amazonaws.services.appstream.AmazonAppStreamClientBuilder;
import com.amazonaws.services.appstream.model.*;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Service
@NoArgsConstructor
public class AppStreamService {
    /* 에러 메세지를 구분할 필요는 있을 듯 하다*/

    AmazonAppStream amazonAppStream;

    @Value("${spring.cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${spring.cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public void setAppStream(){
        try{
            AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

            amazonAppStream = AmazonAppStreamClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(this.region)
                    .build();
        }catch(Exception e){
            log.info(e.getMessage());
        }
    }

    public void createImage(String imgName){
        try {
            CreateImageBuilderRequest createImageRequest = new CreateImageBuilderRequest()
                    .withImageName(imgName);

            CreateImageBuilderResult result = amazonAppStream.createImageBuilder(createImageRequest);

            log.info(result.toString());
        } catch (Exception e){
            log.info(e.getMessage());
        }
    }

    public void startImageBuilder(String imgName){
        try {
            StartImageBuilderRequest startRequest = new StartImageBuilderRequest().withName(imgName);
            StartImageBuilderResult result = amazonAppStream.startImageBuilder(startRequest);

            log.info(result.toString());
        } catch (Exception e){
            log.info(e.getMessage());
        }
    }

    public void stopImageBuilder(String imgName){
        try {
            StopImageBuilderRequest stopRequest = new StopImageBuilderRequest().withName(imgName);
            StopImageBuilderResult result = amazonAppStream.stopImageBuilder(stopRequest);

            log.info(result.toString());
        } catch (Exception e){
            log.info(e.getMessage());
        }
    }

    /**
     * Appstream fleet 생성
     * input
     * - fleetName : 새로 생성할 fleet 이름
     * 항목을 어떻게 사용할지에 대한 검토는 필요할 듯
     *
     * @author sym0417
     * @date 2021-01-15
     */
    public void createFleet(String imageName, String fleetName){
        try {
            DescribeImagesRequest describeImagesRequest = new DescribeImagesRequest();
            DescribeImagesResult result = amazonAppStream.describeImages(describeImagesRequest);

            Image image = null;
            List<Image> images = result.getImages();
            for (int i = 0; i < images.size(); ++i) {
                if (images.get(i).getName().equals(imageName)) {
                    image = images.get(i);
                    break;
                }
            }

            DescribeImageBuildersRequest describeImageBuildersRequest = new DescribeImageBuildersRequest();
            ImageBuilder imageBuilder = amazonAppStream.describeImageBuilders(describeImageBuildersRequest).getImageBuilders().get(0);

            VpcConfig vpcConfig = imageBuilder.getVpcConfig();

            if (image == null)
                return;

            //실질적 사용 부분
            CreateFleetRequest createFleetRequest = new CreateFleetRequest()
                    .withName(fleetName)
                    .withDisplayName(fleetName)
                    .withDescription(fleetName)
                    .withImageName(imageName)
                    .withInstanceType("stream.graphics.g4dn.xlarge")
                    .withFleetType("ON_DEMAND")
                    .withVpcConfig(vpcConfig)
                    .withComputeCapacity(new ComputeCapacity().withDesiredInstances(1));

            CreateFleetResult createFleetResult = amazonAppStream.createFleet(createFleetRequest);

            log.info(createFleetResult.toString());
        }catch(Exception e){
            log.info(e.getMessage());
        }
    }

    /**
     * Appstream fleet 제거
     * input
     * - fleetName : 제거할 fleet 이름
     *
     * @author sym0417
     * @date 2021-01-15
     */
    public void deleteFleet(String fleetName) {
        try {
            DeleteFleetRequest deleteFleetRequest = new DeleteFleetRequest()
                    .withName(fleetName);

            DeleteFleetResult deleteFleetResult = amazonAppStream.deleteFleet(deleteFleetRequest);

            log.info(deleteFleetResult.toString());
        } catch (Exception e){
            log.info(e.getMessage());
        }
    }

    /**
     * Appstream stack 생성
     * input
     * - stackName : 생성할 Stack 이름
     * - fleetName : 연결한 Fleet 이름
     *
     * @author sym0417
     * @date 2021-01-15
     */
    public void createStack(String stackName, String fleetName) {
        try {
            CreateStackRequest createStackRequest = new CreateStackRequest()
                    .withName(stackName)
                    .withDisplayName(stackName)
//                    .withRedirectURL() // 이 부분 redirect 시 URL 뭐 넣을지 정하면 될 듯
                    .withStorageConnectors()
                    ;

            CreateStackResult createStackResult = amazonAppStream.createStack(createStackRequest);

            log.info(createStackResult.toString());

            AssociateFleetRequest associateFleetRequest = new AssociateFleetRequest()
                    .withStackName(stackName)
                    .withFleetName(fleetName);

            AssociateFleetResult associateFleetResult = amazonAppStream.associateFleet(associateFleetRequest);

            log.info(associateFleetResult.toString());
        } catch (Exception e){
            log.info(e.getMessage());
        }
    }

    /**
     * Appstream stack 제거
     * input
     * - stackName : 제거할 Stack 이름
     * - fleetName : 연결된 Fleet 이름
     *
     * @author sym0417
     * @date 2021-01-15
     */
    public void deleteStack(String stackName, String fleetName) {
        try {
            //스택 정보랑 fleet정보를 저장해놓을 필요는 있을 듯 연결된 fleet이 있으면 삭제 불가능
            DisassociateFleetRequest disassociateFleetRequest = new DisassociateFleetRequest()
                    .withStackName(stackName)
                    .withFleetName(fleetName);

            DisassociateFleetResult disassociateFleetResult = amazonAppStream.disassociateFleet(disassociateFleetRequest);

            log.info(disassociateFleetResult.toString());

            DeleteStackRequest deleteStackRequest = new DeleteStackRequest()
                    .withName(stackName);

            DeleteStackResult deleteStackResult = amazonAppStream.deleteStack(deleteStackRequest);

            log.info(deleteStackResult.toString());
        } catch (Exception e){
            log.info(e.getMessage());
        }
    }

    /**
     * AppStream userpool에 사용자 생성
     * input
     * - userName : 사용자 이메일
     *
     * @author sym0417
     * @date 2021-01-15
     */
    public void createUser(String userName, String firstName, String lastName, String stackName){
        try {
            //사용자 생성
            CreateUserRequest createUserRequest = new CreateUserRequest()
                    .withUserName(userName)
                    .withFirstName(firstName)
                    .withLastName(lastName)
                    .withAuthenticationType("USERPOOL");

            CreateUserResult createUserResult = amazonAppStream.createUser(createUserRequest);

            log.info(createUserResult.toString());

            //사용자 및 스택 연관정보 생성
            UserStackAssociation userStackAssociation = new UserStackAssociation()
                    .withUserName(userName)
                    .withStackName(stackName)
                    .withAuthenticationType("USERPOOL")
                    .withSendEmailNotification(true);

            BatchAssociateUserStackRequest batchAssociateUserStackRequest = new BatchAssociateUserStackRequest()
                    .withUserStackAssociations(userStackAssociation);

            BatchAssociateUserStackResult batchAssociateUserStackResult = amazonAppStream.batchAssociateUserStack(batchAssociateUserStackRequest);

            log.info(batchAssociateUserStackResult.toString());
        } catch (Exception e){
            log.info(e.getMessage());
        }
    }

    /**
     * AppStream userpool에서 사용자 삭제
     * input
     * - userName : 사용자 이메일
     *
     * @author sym0417
     * @date 2021-01-15
     */
    public void deleteUser(String userName){
        try{
            DeleteUserRequest deleteUserRequest = new DeleteUserRequest()
                    .withUserName(userName)
                    .withAuthenticationType("USERPOOL");
            DeleteUserResult deleteUserResult = amazonAppStream.deleteUser(deleteUserRequest);

            log.info(deleteUserResult.toString());
        } catch(Exception e){
            log.info(e.getMessage());
        }
    }
}
