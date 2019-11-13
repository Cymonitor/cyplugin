package com;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.JBColor;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.TextUtils;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TranslationAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
     /*   Messages.showMessageDialog("Hello World !", "Information", Messages.getInformationIcon());*/
        final Editor mEditor = anActionEvent.getData(PlatformDataKeys.EDITOR);
        if (null == mEditor) {
            return;
        }
        SelectionModel model = mEditor.getSelectionModel();
        final String selectedText = model.getSelectedText();
        if (TextUtils.isEmpty(selectedText)) {
            return;
        }
        String baseUrl = "http://fanyi.youdao.com/openapi.do?keyfrom=Skykai521&key=977124034&type=data&doctype=json&version=1.1&q="+selectedText;
        URL url=null;
        InputStream in=null;
        BufferedReader reader = null;
        HttpURLConnection connection=null;
        try{
            url=new URL(baseUrl);
            //1,创建一个子线程，实例化一个HttpURLConnection实例,通过URL对象获得
            connection=(HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            /*
             * 2,然后设置HTTP请求方式
             *     通常有GET和POST请求，GET请求常用于从服务器获取数据，POST请求常用于向服务器提交数据
             */
            connection.setRequestMethod(HttpGet.METHOD_NAME);
            //3,再然后，我们可以进行一些自由的设置，比如设置连接超时，读取超时的毫秒数等
            connection.setConnectTimeout(120000);
            //4,这步完成后，基本可以开始接收从服务器返回的输入流了
            in = connection.getInputStream();
            //5,对获取的数据流进行相关的操作，我这里是直接把它显示在TextView上
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder response = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                response.append(line);
            }
            //调用显示响应结果方法
            showPopupBalloon(mEditor,response.toString());
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //6,最后，调用disconnection()将HTTP连接关闭
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(connection != null)
                connection.disconnect();
        }
    }

    private void showPopupBalloon(final Editor editor, final String result) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                JBPopupFactory factory = JBPopupFactory.getInstance();
                factory.createHtmlTextBalloonBuilder(result, null, new JBColor(new Color(186, 238, 186), new Color(73, 117, 73)), null)
                        .setFadeoutTime(5000)
                        .createBalloon()
                        .show(factory.guessBestPopupLocation(editor), Balloon.Position.below);
            }
        });
    }
}
