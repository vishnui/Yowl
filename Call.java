package vishnu.Indukuri.TextLater;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public  class Call {
	@Element
	private String actId ;
	@Element
	private String id ;
	@Element
	private String number ;
	@Element
	private long time ;
	@Element
	private String recording ;
	@Element
	private String statusCode = "tobecalled" ;
	@Element
	private String blobPath = " " ;
	
	public void setNumber(String num,  String x){
		number = num ;
		id = x ;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public void setDate(long x){
		time = x ;
	}
	public long getDate(){
		return time ;
	}
	public String getRecording() {
		return recording;
	}
	public void setRecording(String recording) {
		this.recording = recording;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setActId(String actId) {
		this.actId = actId;
	}
	public String getActId() {
		return actId;
	}
	public void setBlobPath(String blobPath) {
		this.blobPath = blobPath;
	}
	public String getBlobPath() {
		return blobPath;
	}
}
