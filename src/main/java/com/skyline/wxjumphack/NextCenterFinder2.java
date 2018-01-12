package com.skyline.wxjumphack;

import org.apache.commons.io.FilenameUtils;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.opencv_core;

import java.io.File;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvLoadImage;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 * Author     : zh_zhou
 * Create at  : 2018/1/12 18:39
 * Description:
 */
public class NextCenterFinder2 {
    public void find(File srcImgFile,File destImg) {
        opencv_core.IplImage srcImg = cvLoadImage(srcImgFile.getAbsolutePath());
        Mat src = new Mat(srcImg);
        Mat edge = src.clone();
        Canny(src, edge, 4, 12);



        Scalar White     = new Scalar(255, 255, 255, 255);
        Scalar Red    =  new Scalar(0, 0, 255, 0);
        Scalar Blue    = new Scalar(255, 0, 0, 0);
        Scalar Black    = new Scalar(0, 0, 0, 0);


        MatVector contourVec = new MatVector();
        findContours(edge, contourVec, CV_RETR_EXTERNAL, CHAIN_APPROX_NONE);

        Mat colorDst = new Mat(src.size(), CV_8UC3, Black);

        // Eliminate too short or too long contours
        int lengthMin = 100;
        int lengthMax = 1000;

        for (int i = 0; i < contourVec.size(); i++) {
            Mat counter= contourVec.get(i);
            Mat approx=new Mat();
            approxPolyDP(counter,approx,arcLength(counter,true)*0.02,true);
            Rect rect= boundingRect(approx);
            Point2f point2f=new Point2f(0,0);
            FloatPointer radius=new FloatPointer(1f);
            minEnclosingCircle(approx,point2f,radius);
            drawContours(colorDst,contourVec,i,White);
            rectangle( colorDst, rect, Red );              //画矩形，tl矩形左上角，br右上角
            circle( colorDst, new Point((int)point2f.x(),(int)point2f.y()), (int)radius.get(), Blue );
        }

        cvSaveImage(destImg.getAbsolutePath(),new IplImage(colorDst));
    }



    public static void main(String[] args) {
        NextCenterFinder2 finder2=new NextCenterFinder2();
        File inputFolder=new File("d:\\Users\\zh_zhou\\Desktop\\jump\\input");
        File outPut=new File(inputFolder.getParent(),"output");
        if(!outPut.exists()){
            outPut.mkdirs();
        }
        for (File file : inputFolder.listFiles()) {
            String name=file.getName();
            String extension= FilenameUtils.getExtension(name);
            if(!"png".equalsIgnoreCase(extension)){
                continue;
            }
            finder2.find(file,new File(outPut,name));
        }

    }


}
