package fr.harmonia.tsclientquery.query;

import fr.harmonia.tsclientquery.TSClientQuery;
import fr.harmonia.tsclientquery.channel.EnumChannelProperty;

/*
help channelcreate
Usage: channelcreate channel_name={channelName} [channel_properties...]

Creates a new channel using the given properties and displays its ID.

N.B. The channel_password property needs a hashed password as a value.
The hash is a sha1 hash of the password, encoded in base64. You can
use the "hashpassword" command to get the correct value.

Example:
   channelcreate channel_name=My\sChannel channel_topic=My\sTopic
   cid=16
   error id=0 msg=ok

error id=0 msg=ok

 */
public class ChannelCreateQuery extends NoAnswerQuery {
	public static class ChannelProperty {
		private final EnumChannelProperty property;
		private final Object value;

		/**
		 * a channel property to send
		 * 
		 * @param property type
		 * @param value    value, {@link EnumChannelProperty#password} value shouldn't
		 *                 be prehash
		 */
		public ChannelProperty(EnumChannelProperty property, Object value) {
			this.property = property;
			this.value = value;
		}

		public EnumChannelProperty getProperty() {
			return property;
		}

		public Object getValue() {
			if (property == EnumChannelProperty.password)
				return TSClientQuery.hashPassword(String.valueOf(value));
			else
				return value;
		}
	}

	public ChannelCreateQuery(String name, ChannelProperty... properties) {
		super("channelcreate");
		addArgument(EnumChannelProperty.name.getPropertyName(false), name);
		for (ChannelProperty prop : properties)
			addArgument(prop.getProperty().getPropertyName(false), prop.getValue());
	}

}
