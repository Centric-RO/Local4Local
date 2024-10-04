import fonts from "@/constants/font-family";
import { StackNavigationOptions } from "@react-navigation/stack";
import { IconButton } from "react-native-paper";
import paperIcons from "@/constants/paper-icons";

const STACK_OPTIONS: StackNavigationOptions = {
	headerTitleAlign: 'center',
	headerTitleStyle: {
		fontFamily: fonts.MEDIUM,
		fontSize: 16
	},
	headerShadowVisible: false,
	headerBackTitleVisible: false,
	headerBackImage: () => (
		<IconButton icon={paperIcons.ARROW_LEFT} />
	)
};
export default STACK_OPTIONS;