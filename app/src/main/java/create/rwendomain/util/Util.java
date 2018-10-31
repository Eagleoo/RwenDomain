package create.rwendomain.util;

import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import create.rwendomain.R;
import create.rwendomain.model.Domain;

public class Util {



    public static boolean isURL2(String str){
        String regex = "^((https|http|ftp|rtsp|mms)?://)"
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
//                 + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,4})?" // 端口- :80
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        return match( regex ,str );


    }
    /**
     * @param regex
     * 正则表达式字符串
     * @param str
     * 要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


    public static boolean isIP(String addr)     {
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr))
        {
            return false;
        }       /**        * 判断IP格式和范围        */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        boolean ipAddress = mat.find();
        //============对之前的ip判断的bug在进行判断
        if (ipAddress==true){
            String ips[] = addr.split("\\.");
            if(ips.length==4){
                try{
                    for(String ip : ips){
                        if(Integer.parseInt(ip)<0||Integer.parseInt(ip)>255){
                            return false;
                        }
                    }
                }catch (Exception e){
                    return false;
                }
                return true;
            }else{
                return false;
            }
        }
        return ipAddress;     }


    public static String getHtml(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if(conn.getResponseCode() == 200){
            InputStream inputStream = conn.getInputStream();
            byte[] data = read(inputStream); //将流转换成字节数组，read(...)方法的代码在下面

            String html = new String(data, "utf-8"); //将字节数组转换成字符串
            return html;
        }
        return null;
    }

    public static byte[ ] read(InputStream inStream) throws IOException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[ ] buffer = new byte[1024];
        int len = 0;
        while((len = inStream.read(buffer)) != -1){
            outputStream.write(buffer, 0, len);
        }
        inStream.close();
        return outputStream.toByteArray();
    }

    public static boolean checkAccountMark(String account){
        String all = "^[a-zA-Z0-9_ ]";
        Pattern pattern = Pattern.compile(all);
        return pattern.matches(all,account);
    }

    public static void setImage(ImageView icon,String name){
        if (name.equals("人文网")){
            icon.setImageResource(R.drawable.logo1);
        }
        else if (name.equals("腾讯云")){
            icon.setImageResource(R.drawable.logo2);
        }
        else if (name.equals("阿里云")){
            icon.setImageResource(R.drawable.logo3);
        }
        else if (name.equals("华为云")){
            icon.setImageResource(R.drawable.logo4);
        }
        else if (name.equals("网易云")){
            icon.setImageResource(R.drawable.logo5);
        }
        else if (name.equals("西部数码")){
            icon.setImageResource(R.drawable.logo6);
        }
        else if (name.equals("商务中国")){
            icon.setImageResource(R.drawable.logo7);
        }
        else if (name.equals("中国数据")){
            icon.setImageResource(R.drawable.logo8);
        }
        else if (name.equals("纳网")){
            icon.setImageResource(R.drawable.logo9);
        }
        else if (name.equals("时代互联")){
            icon.setImageResource(R.drawable.logo10);
        }
        else if (name.equals("世纪东方")){
            icon.setImageResource(R.drawable.logo11);
        }
        else {
            icon.setImageResource(R.drawable.logo1);
        }
    }

    public static void setTextView(TextView address, String choose){
        if (choose.equals("华为云")){
            address.setText(Util.getUrl("华为云"));
        }
        else if (choose.equals("阿里云")){
            address.setText(Util.getUrl("阿里云"));
        }
        else if (choose.equals("腾讯云")){
            address.setText(Util.getUrl("腾讯云"));
        }
        else if (choose.equals("网易云")){
            address.setText(Util.getUrl("网易云"));
        }
        else if (choose.equals("西部数码")){
            address.setText(Util.getUrl("西部数码"));
        }
        else if (choose.equals("商务中国")){
            address.setText(Util.getUrl("商务中国"));
        }
        else if (choose.equals("中国数据")){
            address.setText(Util.getUrl("中国数据"));
        }
        else if (choose.equals("纳网")){
            address.setText(Util.getUrl("纳网"));
        }
        else if (choose.equals("西维")){
            address.setText(Util.getUrl("西维"));
        }
        else if (choose.equals("中资源")){
            address.setText(Util.getUrl("中资源"));
        }
        else if (choose.equals("sun接口")){
            address.setText(Util.getUrl("sun接口"));
        }
        else if (choose.equals("新网")){
            address.setText(Util.getUrl("新网"));
        }
        else if (choose.equals("时代互联")){
            address.setText(Util.getUrl("时代互联"));
        }
        else if (choose.equals("世纪东方")){
            address.setText(Util.getUrl("世纪东方"));
        }else if (choose.equals("自定义")){
            address.setText("http://");
        }

        else {
            address.setText(Util.getUrl("人文网"));
        }
    }

    public static String getUrl(String s) {
        HashMap<String , String> map = new HashMap<String , String>();
        map.put("人文网","http://www.admin.gvk.cn/");
        map.put("华为云","https://activity.huaweicloud.com/");
        map.put("阿里云","https://dc.aliyun.com/login/loginx?spm=5176.8208715.110.2.59164ae8VegLfQ");
        map.put("腾讯云","https://cloud.tencent.com/");
        map.put("网易云","http://netease.im/");
        map.put("西部数码","https://www.west.cn/login.asp");
        map.put("商务中国","http://dnsmsn.com/");
        map.put("中国数据","http://dns.4cun.com/info_see.asp");
        map.put("纳网","http://www.nwabc.cn/site/login/lang/zh_cn");
        map.put("西维","http://www.myhostadmin.net/");
        map.put("中资源","http://domain.cnolnic.com/");
        map.put("sun接口","http://www.ufhost.com/");
        map.put("新网","http://dcp.xinnet.com/Modules/agent/domain/domain_manage.jsp");
        map.put("时代互联","http://www.uvip.cn/");
        map.put("世纪东方","http://domain.client.cdnhost.cn/conpanel/domain/ConPanelDomainLoginAction!redirectToLogin.action");

        return map.get(s);
    }

    public static String getDayStr(String day,String end) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = null;
        Date oldDate = null;
        try {
            nowDate = df.parse(day);
            oldDate=df.parse(end);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        //如果需要向后计算日期 -改为+
        Date newDate2 = new Date(oldDate.getTime() - nowDate.getTime());
        String dateOk = String.valueOf(newDate2.getTime()/(1000*3600*24));

        return dateOk;
    }

    public static String getDateStr(String day,String end) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = null;
        Date oldDate = null;
        try {
            nowDate = df.parse(day);
            oldDate=df.parse(end);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        //如果需要向后计算日期 -改为+
        Date newDate2 = new Date(oldDate.getTime() - nowDate.getTime());
        String dateOk = String.valueOf(newDate2.getTime()/(1000*3600*24));

        if (newDate2.getTime()<0){
            return "已到期";
        }
        return formatDate(Integer.valueOf(dateOk));
    }

    public static String formatDate(int s){
        int year=0,month=0,day=0;
        if (s/365!=0){
            if (((s%365)/30)!=0){
                month=(s%365)/30;
            }
            year=s/365;
            day=(s%365)%30;
            if (month==0){
                if (day==0){
                    return year+"年";
                }
                return year+"年"+day+"天";
            }
            else if (day==0){
                return year+"年"+month+"月";
            }

            return year+"年"+month+"月"+day+"天";
        }
        else if ((s%365)/30!=0){
            month=(s%365)/30;
            day=(s%365)%30;
            if (day==0){
                return month+"月";
            }
            return month+"月"+day+"天";
        }
        else {
            day=(s%365)%30;
        }
        return day+"天";
    }

    public static String getDate(String day,int Num) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = null;
        try {
            nowDate = df.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //如果需要向后计算日期 -改为+
        Date newDate2 = new Date(nowDate.getTime() + (long)Num * 24 * 60 * 60 * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateOk = simpleDateFormat.format(newDate2);
        return dateOk;
    }

    public static String selecttime(String domain) {
        // 条件查询
        List<Domain> list = new Select().from(Domain.class)
                .where("domain=?", domain).execute();
        return list.get(0).getUtil_time();

    }

}
