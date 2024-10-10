import { useFonts } from "expo-font";
import RootLayout from "@/app/_layout";
import { act, fireEvent, render, waitFor } from "@testing-library/react-native";
import AsyncStorage from "@react-native-async-storage/async-storage";
import { JsStack } from "@/js-stack";
import STACK_OPTIONS from "@/constants/stack-config";
import i18n from "@/i18n";

const mockOnPress = jest.fn();

jest.mock('react-native-paper', () => {
	const { Text, Pressable } = require('react-native');
	const actual = jest.requireActual('react-native-paper');

	return {
		...actual,
		IconButton: ({ icon }: any) => (
			<Pressable onPress={mockOnPress}>
				<Text>{icon}</Text>
			</Pressable>
		)
	};
});

jest.mock('expo-router', () => {
	const { View, Text } = require('react-native');

	return {
		Stack: {
			Screen: (({ options }: any) => (
				<View>
					<Text>{options.headerTitle}</Text>
					{options.headerRight && <options.headerRight />}
				</View>
			))
		},
		Link: ({ children }: any) => (children)
	};
});

jest.mock('react-i18next', () => {
	const { View } = require('react-native');

	return {
		I18nextProvider: (({ children }: any) => (
			<View>
				{children}
			</View>
		)),
		useTranslation: () => ({
			t: jest.fn((key) => key),
			changeLanguage: jest.fn()
		})
	};
});

jest.mock('@/i18n', () => ({
	changeLanguage: jest.fn()
}));

jest.mock('expo-font', () => ({
	useFonts: jest.fn()
}));

jest.mock('@react-native-async-storage/async-storage', () => ({
	getItem: jest.fn()
}));

jest.mock('@/js-stack', () => {
	const { View } = require('react-native');

	return {
		JsStack: jest.fn(({ children, screenOptions }) => (
			<View>
				{children}
			</View>
		))
	};
});

describe('<RootLayout />', () => {
	it('should render properly when fonts are loaded', async () => {
		(useFonts as jest.Mock).mockReturnValue([true, null]);

		const { getByText } = render(<RootLayout />);

		await waitFor(() => {
			expect(getByText('title.merchants')).toBeTruthy();
			expect(getByText('title.changeLanguage')).toBeTruthy();
			expect(JsStack).toHaveBeenCalledWith(expect.objectContaining({ screenOptions: STACK_OPTIONS }), expect.anything())
		});
	});

	it('should navigate to change-language when globe icon is pressed', async () => {
		(useFonts as jest.Mock).mockReturnValue([true, null]);

		const { getByText } = render(<RootLayout />);

		await act(() => {
			fireEvent(getByText('web'), 'press');
		});

		expect(mockOnPress).toHaveBeenCalled();
	});

	it('should not render anything until the fonts have loaded', async () => {
		(useFonts as jest.Mock).mockReturnValue([false, null]);

		const { queryByText } = render(<RootLayout />);

		await waitFor(() => {
			expect(queryByText('title.merchants')).toBeNull();
		});
	});

	it('should log an error if language cannot be fetched', async () => {
		(useFonts as jest.Mock).mockReturnValue([true, null]);
		(AsyncStorage.getItem as jest.Mock).mockRejectedValueOnce(new Error('error'));
		jest.spyOn(console, 'log').mockImplementation(jest.fn());

		render(<RootLayout />);

		await waitFor(() => {
			expect(AsyncStorage.getItem).toHaveBeenCalledWith('language');
			expect(console.log).toHaveBeenCalledWith(expect.any(Error));
		});
	});

	it('should change the language if the user has previously set it', async () => {
		(useFonts as jest.Mock).mockReturnValue([true, null]);
		(AsyncStorage.getItem as jest.Mock).mockReturnValueOnce('language');

		render(<RootLayout />);

		await waitFor(() => {
			expect(i18n.changeLanguage).toHaveBeenCalledWith('language');
		});
	});
});