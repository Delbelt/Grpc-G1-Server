package server.handlers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import com.google.protobuf.Timestamp;
import com.google.type.Date;

public class TimestampGrpcConverter {

	public static Date toProtoDate(LocalDate localDate) {
		
		return Date
				.newBuilder()
				.setYear(localDate.getYear())
				.setMonth(localDate.getMonthValue())
				.setDay(localDate.getDayOfMonth())
				.build();
	}

	public static Timestamp toProtoTimestamp(LocalDateTime localDateTime) {

		Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
	    
	    return Timestamp.newBuilder()
	                    .setSeconds(instant.getEpochSecond())
	                    .setNanos(instant.getNano())
	                    .build();
	}
}
