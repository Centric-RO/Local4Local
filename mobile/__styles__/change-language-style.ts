import { StyleSheet } from "react-native";
import fonts from "@/constants/font-family";
import colors from "@/constants/colors";

const styles = StyleSheet.create({
	container: {
		flex: 1
	},
	list: {
		paddingTop: 8
	},
	listEntry: {
		flexDirection: 'row',
		padding: 16,
		gap: 8,
		alignItems: 'center',
	},
	listDivider: {
		marginHorizontal: 16,
		backgroundColor: colors.NEUTRAL_100,
		height: 1
	},
	flagContainer: {
		paddingVertical: 4
	},
	flag: {
		width: 24,
		height: 16,
	},
	languageTitle: {
		fontSize: 16,
		lineHeight: 24,
		fontFamily: fonts.REGULAR,
		flex: 1
	}
});

export default styles;